package emma.view.swing;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import emma.petri.view.PropertiesView;
import emma.view.swing.petri.table.PlaceTableModel;

public class PropertiesPanel extends DesktopFrame implements PropertiesView{
	private static final long serialVersionUID = -3252042432305047846L;
	
	private JTable table;
	private JComboBox<String> placeTypes;
	public PropertiesPanel(SwingController control){
		super("Element Properties",true, false, false,true);
		this.setSize(200,200);
		table = new JTable();
		table.setVisible(false);
		this.getContentPane().add(table);
		control.setPropertiesView(this);

		this.placeTypes = new JComboBox<String>();
		this.placeTypes.addItem(emma.model.resources.L.class.getSimpleName());
		this.placeTypes.addItem(emma.model.resources.S.class.getSimpleName());
	}
	
	@Override
	public void setProperties(TableModel data){
		if(data==null){
			table.setVisible(false);
		}
		else{
			table.setModel(data);
			if(data instanceof PlaceTableModel && data.getRowCount()==4){
				table.getColumnModel().getColumn(1).setCellEditor(new CellEditor(true));
			}
			else{
				table.getColumnModel().getColumn(1).setCellEditor(new CellEditor(false));
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
	    private static final DefaultCellEditor place = new DefaultCellEditor(new JComboBox<String>(new String[]{emma.model.resources.L.class.getSimpleName(),emma.model.resources.S.class.getSimpleName()}));
	    private DefaultCellEditor lastSelected;

	    private boolean isNullPlace;
	    
	    public CellEditor(boolean isNullPlace){
	    	this.isNullPlace=isNullPlace;
	    }
	    
	    @Override
	    public Object getCellEditorValue(){
	        return lastSelected.getCellEditorValue();
	    }

	    @Override
	    public Component getTableCellEditorComponent(JTable table,
	            Object value, boolean isSelected, int row, int column) {
	        if(isNullPlace && row == 3){
	        	lastSelected=place;
	        }
	        else{
	        	lastSelected=classic;
	        }
	        return lastSelected.getTableCellEditorComponent(table, value, isSelected, row, column);
	    }
	}
}
