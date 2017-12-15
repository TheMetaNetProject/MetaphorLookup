package edu.berkeley.icsi.metanet.metalookup;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

// an example tab
public class Main extends AbstractOWLViewComponent {
	private static final long serialVersionUID = 5343269347459987438L;
	
	/* Declaring class variables
	 * an instance of AVis will have a mainPanel which contains the toolBar, selectorPane, and graphComponent
	 */
	private OWLOntology owlModel;
	private SearchPanel searchPanel;
	private JScrollPane resultPanel;
	private EntityLibrary library;
	private JSplitPane mainPanel;
	
	// startup code
	public void initialiseOWLView() throws Exception{
		
		setLayout(new BorderLayout());
		try {
			OWLModelManager manager = getOWLModelManager();
			owlModel = manager.getActiveOntology();
			library = new EntityLibrary(owlModel);
			searchPanel = new SearchPanel(owlModel);
			searchPanel.searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SearchPanelListItem[] selected = new SearchPanelListItem[searchPanel.selectedList.lingListModel.size()];
					searchPanel.selectedList.lingListModel.copyInto(selected);
					//System.out.println(selected.length);
					resultPanel = new ResultTable(owlModel, selected, library);
					mainPanel.setRightComponent(resultPanel);
					mainPanel.setDividerLocation(300);
					mainPanel.setResizeWeight(0.0);
					mainPanel.setDividerSize(0);
					mainPanel.validate();
				}
			});
			
			mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			mainPanel.setDividerLocation(300);
			mainPanel.setResizeWeight(0.0);
			mainPanel.setDividerSize(0);
			mainPanel.setLeftComponent(searchPanel);
			mainPanel.setRightComponent(new ResultTable());
			
			add(mainPanel, BorderLayout.CENTER);
			
		} catch (Exception e) {
			JLabel error = new JLabel("There was an error loading the plugin, please double check that you have the correct Metaphor repository loaded and try again");
			add(error, BorderLayout.PAGE_START);
			JButton refresh = new JButton("Reload");
			refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						initialiseOWLView();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			add(refresh, BorderLayout.CENTER);
		}
		
	}

	@Override
	protected void disposeOWLView() {
		owlModel = null;
	}
	
}

