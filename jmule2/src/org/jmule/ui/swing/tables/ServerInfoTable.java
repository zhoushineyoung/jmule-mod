/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.ui.swing.tables;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jmule.ui.swing.models.ServerInfoTableModel;

/**
 *
 * Created on Oct 2, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/16 17:35:11 $
 */
public class ServerInfoTable extends JTable {

	// ------------------ Table cell renderers ------------------
	
	class ServerInfoLabelCellRenderer extends JLabel implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
            this.setText((String)value);		
			return this;
		}
	}
	
	class ServerInfoParamCellRenderer extends JLabel implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			this.setText((String)value);
			this.setToolTipText((String)value);
			return this;
		}		
	}
	
	public ServerInfoTable() {
		
		JMTableColumn server_info_label_column = new JMTableColumn();
		server_info_label_column.setModelIndex(0);
		server_info_label_column.setHeaderValue(" ");
		server_info_label_column.setCellRenderer(new ServerInfoLabelCellRenderer());
		
		JMTableColumn server_info_param_column = new JMTableColumn();
		server_info_param_column.setModelIndex(1);
		server_info_param_column.setHeaderValue(" ");
		server_info_param_column.setCellRenderer(new ServerInfoParamCellRenderer());
		
		TableColumnModel column_model = new DefaultTableColumnModel();
		column_model.addColumn(server_info_label_column);
		column_model.addColumn(server_info_param_column);
		
		this.setModel(new ServerInfoTableModel());
		this.setColumnModel(column_model);
	}
}
