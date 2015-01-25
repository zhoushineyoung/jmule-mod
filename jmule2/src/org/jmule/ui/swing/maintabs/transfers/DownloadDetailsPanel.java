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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.utils.Misc;
import org.jmule.ui.UIConstants;
import org.jmule.ui.swing.common.PiecesPanel;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 *
 * Created on Oct 5, 2008
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: javajox $ on $Date: 2009/09/22 19:08:43 $
 */
public class DownloadDetailsPanel extends JPanel {

	// ---------- Transfers panel -----------------------------
	private JLabel pieces_label = new JLabel("Pieces");
	private JLabel up_speed_value = new JLabel();
	private JLabel transferred_label = new JLabel("Transferred");
	private PiecesPanel pieces_panel;
	private JLabel down_speed_value = new JLabel();
	private JLabel remaining_value = new JLabel();
	private JLabel transferred_value = new JLabel();
	private JLabel partial_sources_value = new JLabel();
	private JLabel complete_sources_value = new JLabel();
	private JLabel upload_speed_label = new JLabel("Up speed");
	private JLabel download_speed_label = new JLabel("Down speed");
	private JLabel remaining_label = new JLabel("Remaining");
	private JLabel partial_sources_label = new JLabel("Partial sources");
	private JLabel complete_sources_label = new JLabel("Complete sources");
	
	
	// ------------- File details panel ------------------------------
	private JLabel file_name_label = new JLabel("File name");
	private JLabel status_label = new JLabel("Status");
	private JLabel file_hash_label = new JLabel("File hash");
	private JLabel parts_label = new JLabel("Parts");
	private JLabel ed2k_link_label = new JLabel("ED2K Link");
	private JLabel file_size_label = new JLabel("File size");
	private JLabel part_met_file_label = new JLabel("part.met");
	private JLabel file_name_value = new JLabel();
	private JLabel file_size_value = new JLabel();
	private JLabel status_value = new JLabel();
	private JLabel file_hash_value = new JLabel();
	private JLabel part_met_file_value = new JLabel();
	private JLabel parts_value = new JLabel();
	private JLabel ed2k_link_value = new JLabel();
	
	private JPanel transfer_panel = new JPanel();
	private JPanel general_info_panel = new JPanel();
		
	private GridLayout grid_layout = new GridLayout(2,1);
	
    private final static Font label_value_font = new java.awt.Font("Dialog", 0, 12);
    private final static Font label_font = new java.awt.Font("Dialog", 1, 12);
	
	private DownloadSession session;
	private JMuleCore _core = JMuleCoreFactory.getSingleton();
	private UploadManager _upload_manager = _core.getUploadManager();
	
	public DownloadDetailsPanel(DownloadSession session) {
		this.session = session;
		init();
	}
	
	private void init() {
		
		this.setLayout(grid_layout);
		
		// ----------------- Transfers panel --------------------------------
		GridBagLayout transfer_grid_bag_layout = new GridBagLayout();
		transfer_grid_bag_layout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
		transfer_grid_bag_layout.rowHeights = new int[] {10, 24, 24, 24, 24, 24, 24, 24, 7};
		transfer_grid_bag_layout.columnWeights = new double[] {0.0, 0.0, 0.1};
		transfer_grid_bag_layout.columnWidths = new int[] {111, 17, 7};
		
		pieces_label.setFont(label_font);
		transferred_label.setFont(label_font);
		upload_speed_label.setFont(label_font);
		download_speed_label.setFont(label_font);
		remaining_label.setFont(label_font);
		partial_sources_label.setFont(label_font);
		complete_sources_label.setFont(label_font);
		
		pieces_label.setHorizontalAlignment(SwingConstants.RIGHT);
		transferred_label.setHorizontalAlignment(SwingConstants.RIGHT);
		upload_speed_label.setHorizontalAlignment(SwingConstants.RIGHT);
		download_speed_label.setHorizontalAlignment(SwingConstants.RIGHT);
		remaining_label.setHorizontalAlignment(SwingConstants.RIGHT);
		partial_sources_label.setHorizontalAlignment(SwingConstants.RIGHT);
		complete_sources_label.setHorizontalAlignment(SwingConstants.RIGHT);
		
		transferred_value.setFont(label_value_font);
		up_speed_value.setFont(label_value_font);
		down_speed_value.setFont(label_value_font);
		remaining_value.setFont(label_value_font);
		partial_sources_value.setFont(label_value_font);
		complete_sources_value.setFont(label_value_font);
		
		pieces_panel = new PiecesPanel(session);
		ed2k_link_value.setForeground(Color.BLUE);
		ed2k_link_value.setToolTipText("Click to copy ed2k link");
		file_hash_value.setForeground(Color.BLUE);
		
		transfer_panel.setBorder(BorderFactory.createTitledBorder("Transfer"));
		transfer_panel.setLayout(transfer_grid_bag_layout);
		transfer_panel.setPreferredSize(new java.awt.Dimension(436, 202));
		transfer_panel.add(pieces_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(complete_sources_label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(partial_sources_label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(transferred_label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(remaining_label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(download_speed_label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(upload_speed_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(pieces_panel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
		transfer_panel.add(complete_sources_value, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(partial_sources_value, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(transferred_value, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(remaining_value, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(down_speed_value, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		transfer_panel.add(up_speed_value, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(transfer_panel);
		
		file_name_label.setFont(label_font);
		status_label.setFont(label_font);
		file_hash_label.setFont(label_font);
		parts_label.setFont(label_font);
		ed2k_link_label.setFont(label_font);
		file_size_label.setFont(label_font);
		part_met_file_label.setFont(label_font);
		
		file_name_value.setFont(label_value_font);
		status_value.setFont(label_value_font);
		file_hash_value.setFont(label_value_font);
		parts_value.setFont(label_value_font);
		ed2k_link_value.setFont(label_value_font);
		file_size_value.setFont(label_value_font);
		part_met_file_value.setFont(label_value_font);
		
		file_name_label.setHorizontalAlignment(SwingConstants.RIGHT);
		status_label.setHorizontalAlignment(SwingConstants.RIGHT);
		file_hash_label.setHorizontalAlignment(SwingConstants.RIGHT);
		parts_label.setHorizontalAlignment(SwingConstants.RIGHT);
		ed2k_link_label.setHorizontalAlignment(SwingConstants.RIGHT);
		file_size_label.setHorizontalAlignment(SwingConstants.RIGHT);
		part_met_file_label.setHorizontalAlignment(SwingConstants.RIGHT);
		
		GridBagLayout general_info_ayout = new GridBagLayout();
		general_info_panel.setPreferredSize(new java.awt.Dimension(382, 185));
		general_info_ayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
		general_info_ayout.rowHeights = new int[] {26, 25, 24, 26, 24, 25, 26, 7};
		general_info_ayout.columnWeights = new double[] {0.0, 0.0, 0.1};
		general_info_ayout.columnWidths = new int[] {90, 15, 7};
		general_info_panel.setLayout(general_info_ayout);
		general_info_panel.add(file_name_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(part_met_file_label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(status_label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(file_size_label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(ed2k_link_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(parts_label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(file_hash_label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(file_name_value, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(ed2k_link_value, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(part_met_file_value, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(file_hash_value, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(status_value, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(file_size_value, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		general_info_panel.add(parts_value, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        general_info_panel.setBorder(BorderFactory.createTitledBorder("File info"));
		this.add(general_info_panel);
		file_name_value.setIcon(new ImageIcon(UIConstants.getMimeURLByExtension(Misc.getFileExtension(session.getSharingName()))));
		updateData();
	}
	
	public void updateData() {
		try {
			pieces_panel.repaint();
			complete_sources_value.setText(session.getCompletedSources() + "");
			partial_sources_value.setText(session.getPartialSources() + "");
			transferred_value.setText(FileFormatter.formatFileSize(session.getTransferredBytes()));
			remaining_value.setText(FileFormatter.formatFileSize(session.getFileSize() - 
					                 session.getTransferredBytes()));
			down_speed_value.setText(SpeedFormatter.formatSpeed(session.getSpeed()));
			up_speed_value.setText(_upload_manager.hasUpload(session.getFileHash()) ?
					                   SpeedFormatter.formatSpeed(_upload_manager.getUpload(session.getFileHash()).getSpeed()) :
					            	   SpeedFormatter.formatSpeed(0) );
			file_name_value.setText(session.getSharingName());
			file_name_value.setToolTipText(session.getSharingName());
			ed2k_link_value.setText(session.getED2KLink().getAsString());
			part_met_file_value.setText(session.getTempFileName());
			part_met_file_value.setToolTipText(session.getMetFilePath());
			file_hash_value.setText(session.getFileHash().getAsString());
			if (session.isStarted())
				status_value.setText("Started");
			else
				status_value.setText("Stopped");
			file_size_value.setText(FileFormatter.formatFileSize(session.getFileSize()));
			parts_value.setText(session.getPartCount() + ", available " + session.getAvailablePartCount());
		}catch( Throwable cause ) {
			cause.printStackTrace();
		}
	}

}
