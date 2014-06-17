package emma.view.swing.petri;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import emma.petri.view.PropertiesView;
import emma.tools.ClassFounder;
import emma.view.swing.petri.table.PlaceTableModel;

public class PropertiesPanel extends DesktopFrame implements PropertiesView{
	private static final long serialVersionUID = -3252042432305047846L;
	
	private JTable table;
	private static JComboBox<String> placeTypes = new JComboBox<String>(ClassFounder.getClassesSimpleName(ClassFounder.getUnmappedResourcePackage()));
	
	public PropertiesPanel(SwingController control){
		super("Element Properties",true, false, false,true);
		this.setSize(120,200);
		table = new JTable();
		table.setVisible(false);
		this.getContentPane().add(table);
		control.setPropertiesView(this);
	}
	
	@Override
	public void setProperties(TableModel data){
		if(data==null){
			table.setVisible(false);
		}
		else{
			table.setModel(data);
			if(data instanceof PlaceTableModel){
				table.getColumnModel().getColumn(1).setCellEditor(new CellEditor());
			}
			table.setVisible(true);
		}
	}
	
	private static class CellEditor extends AbstractCellEditor implements TableCellEditor {

	    /**
		 * 
		 */
		private static final long serialVersionUID = -280797455779869064L;
		
		private static final DefaultCellEditor classic = new DefaultCellEditor(new JTextField());
	    private static final DefaultCellEditor place = new DefaultCellEditor(placeTypes);
	    private static final DefaultCellEditor chbox = new DefaultCellEditor(new JCheckBox());
	    private DefaultCellEditor lastSelected;
	    
	    @Override
	    public Object getCellEditorValue(){
	        return lastSelected.getCellEditorValue();
	    }

	    @Override
	    public Component getTableCellEditorComponent(JTable table,
	            Object value, boolean isSelected, int row, int column) {
	        switch(row){
	        case 4:
	        	lastSelected=place;
	        	break;
	        case 3:
	        case 5:
	        	lastSelected=chbox;
	        	break;
	        default:
	        	lastSelected=classic;
	        	break;
	        }
	        return lastSelected.getTableCellEditorComponent(table, value, isSelected, row, column);
	    }
	}
}
