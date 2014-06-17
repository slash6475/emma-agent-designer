package emma.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import emma.model.resources.Resource;
import emma.petri.model.resources.UnmappedResource;

public class ClassFounder {
	
	
	public static String[] getClassesSimpleName(String packageName){
		return getClassesSimpleName(packageName,false);
	}
	
	@SuppressWarnings("rawtypes")
	public static String[] getClassesSimpleName(String packageName, boolean withInterface){
		List<String> className = new ArrayList<>();
		Iterator<Class> it;
		try {
			it = getClasses(packageName,withInterface).iterator();
			while(it.hasNext()){
				className.add(it.next().getSimpleName());
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return className.toArray(new String[className.size()]);
	}
	/**
	02.* Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	03.*
	04.* @param packageName The base package
	05.* @return The classes
	06.* @throws ClassNotFoundException
	07.* @throws IOException
	08.*/
	@SuppressWarnings("rawtypes")
	public static List<Class> getClasses(String packageName, boolean withInterface) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName,withInterface));
		}
		return classes;
		}
	/**
	* Recursive method used to find all classes in a given directory and subdirs.
	*
	* @param directory   The base directory
	* @param packageName The package name for classes found inside the base directory
	* @return The classes
	* @throws ClassNotFoundException
	*/
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName, boolean withInterface) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName(),withInterface));
			}
			else if (file.getName().endsWith(".class")) {
				Class c = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
				if(withInterface || !c.isInterface()){
					classes.add(c);
				}
			}
		}
		return classes;
	}

	public static String getResourcePackage(){
		return emma.model.resources.Resource.class.getPackage().getName();
	}
	
	public static String getUnmappedResourcePackage(){
		return emma.petri.model.resources.UnmappedResource.class.getPackage().getName();
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Resource> getResourceClass(String type) throws ClassNotFoundException{
		return (Class<? extends Resource>) Class.forName(getResourcePackage()+"."+type);
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends UnmappedResource> getUnmappedResourceClass(String type) throws ClassNotFoundException{
		return (Class<? extends UnmappedResource>) Class.forName(getUnmappedResourcePackage()+"."+type);
	}
}
