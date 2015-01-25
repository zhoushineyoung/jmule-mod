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
package org.jmule.ui.swing.maintabs.transfers;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.ui.utils.TimeFormatter;

/**
 *
 * Created on Oct 11, 2008
 * @author javajox
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/17 14:53:43 $
 */
public class UploadDetailsPanel extends JPanel {

	private JLabel file_name_label = new JLabel("File name");
	private JLabel file_hash_label = new JLabel("File hash");
	private JLabel peers_value = new JLabel();
	private JLabel eta_value = new JLabel();
	private JLabel transferred_value = new JLabel();
	private JLabel transferred_label = new JLabel("Transferred");
	private JLabel file_name_value = new JLabel();
	private JLabel upload_speed_value = new JLabel();
	private JLabel ed2k_link_value = new JLabel();
	private JLabel file_size_value = new JLabel();
	private JLabel file_hash_value = new JLabel();
	private JLabel peers_label = new JLabel("Peers");
	private JLabel eta_label = new JLabel("ETA");
	private JLabel upload_speed_label = new JLabel("Upload speed");
	private JLabel ed2k_link_label = new JLabel("ED2K Link");
	private JLabel file_size_label = new JLabel("File size");
	
	private UploadSession session;
	
    private final static Font label_value_font = new java.awt.Font("Dialog", 0, 12);
    private final static Font label_font = new java.awt.Font("Dialog", 1, 12);
	
	public UploadDetailsPanel(UploadSession session) {
		
		this.session = session;
		init();
	}
	
	private void init() {
		GridBagLayout grid_bag_layout = new GridBagLayout();
		this.setPreferredSize(new java.awt.Dimension(439, 254));
		grid_bag_layout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
		grid_bag_layout.rowHeights = new int[] {19, 20, 20, 21, 21, 19, 20, 20, 20, 7};
		grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		grid_bag_layout.columnWidths = new int[] {7, 113, 18, 7};
		
		file_name_label.setFont(label_font);
		file_hash_label.setFont(label_font);
		file_size_label.setFont(label_font);
		ed2k_link_label.setFont(label_font);
		upload_speed_label.setFont(label_font);
		transferred_label.setFont(label_font);
		eta_label.setFont(label_font);
		peers_label.setFont(label_font);
		
		file_name_value.setFont(label_value_font);
		file_hash_value.setFont(label_value_font);
		file_size_value.setFont(label_value_font);
		ed2k_link_value.setFont(label_value_font);
		upload_speed_value.setFont(label_value_font);
		transferred_value.setFont(label_value_font);
		eta_value.setFont(label_value_font);
		peers_value.setFont(label_value_font);
		
		this.setLayout(grid_bag_layout);
		this.add(file_name_label, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(file_hash_label, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(file_size_label, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(ed2k_link_label, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(upload_speed_label, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(transferred_label, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(eta_label, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(peers_label, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(file_name_value, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(file_hash_value, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(file_size_value, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(ed2k_link_value, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(upload_speed_value, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(transferred_value, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(eta_value, new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(peers_value, new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        file_name_value.setIcon(new ImageIcon(UIConstants.getMimeURLByExtension(Misc.getFileExtension(session.getSharingName()))));
        updateData();
	}
	
	public void updateData() {
		file_name_value.setText(session.getSharingName());
		file_hash_value.setText(session.getFileHash().getAsString());
		file_size_value.setText(FileFormatter.formatFileSize(session.getFileSize()));
		ed2k_link_value.setText(session.getED2KLink().getAsString());
		upload_speed_value.setText(SpeedFormatter.formatSpeed(session.getSpeed()));
		transferred_value.setText(FileFormatter.formatFileSize(session.getTransferredBytes()));
		eta_value.setText(TimeFormatter.format(session.getETA()));
		peers_value.setText(session.getPeerCount() + "");
	}

}
