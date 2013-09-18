// This is copyrighted source file, part of Rakiura JFern package. 
// See the file LICENSE for copyright information and the terms and conditions
// for copying, distributing and modifications of Rakiura JFern package.
// Copyright (C) 1999-2009 by Mariusz Nowostawski and others.

package org.rakiura.cpn;

/**/
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for storing and referencing Place objects.
 * 
 *<br><br>
 * PlaceHolder.java<br>
 * Created: Fri Sep 29 12:31:14 2000<br>
 *
 *@author  <a href="mariusz@rakiura.org">Mariusz Nowostawski</a>
 *@version 4.0.0 $Revision: 1.4 $
 *@since 1.0
 */
final class PlaceHolder implements java.io.Serializable {

	
  private static final long serialVersionUID = 3257003254876746034L;
	
  private HashMap map_forName;
  private HashMap map_forID;

  public PlaceHolder() {
    this.map_forName = new HashMap();
    this.map_forID = new HashMap();
  }

  public final PlaceHolder add(Place place){
    this.map_forName.put(place.getName(), place);
    this.map_forID.put(place.getID(), place);
    return this;
  }

  public final Place forName(String name){
    return (Place)this.map_forName.get(name);
  }

  public final Place forID(String id){
    return (Place)this.map_forID.get(id);
  }

  public final Set places(){
    return new HashSet(this.map_forID.values());
  }
  
} // PlaceHolder
//////////////////// end of file ////////////////////
