package edu.berkeley.icsi.metanet.metalookup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public class SearchPanel extends JPanel {
	
	private EntityLibrary library;
	private OWLOntology owlModel;
	protected JPanel searchFilter;
	protected EntityList list;
	protected EntityList selectedList;
	public JButton searchButton;
	
	public SearchPanel(OWLOntology owlModel) {
		this.owlModel = owlModel;
		this.library = new EntityLibrary(owlModel);
		searchFilter = searchFilterColumn();
		
		this.setLayout(new BorderLayout());
		this.add(searchFilter, BorderLayout.LINE_START);
		
	}
	
	private JPanel searchFilterColumn() {
		JPanel searchFilter = new JPanel();
		searchFilter.setLayout(new BoxLayout(searchFilter, BoxLayout.PAGE_AXIS));
		
		JTextField filter = new JTextField();
		//JLabel fLabel = new JLabel("Search");
		filter.setMinimumSize(new Dimension(250, 20));
		filter.setMaximumSize(new Dimension(250, 20));
		Dimension hgap = new Dimension(250,15);
		list = new EntityList(owlModel, library, "LinguisticMetaphor");
		selectedList = new EntityList(library);
		SelectButtons selectButtons = new SelectButtons();
		searchButton = new JButton("Run Search");
		
		//searchFilter.add(fLabel);
		searchFilter.add(filter);
		searchFilter.add(Box.createRigidArea(hgap));
		searchFilter.add(list);
		searchFilter.add(selectButtons);
		searchFilter.add(selectedList);
		searchFilter.add(Box.createRigidArea(hgap));
		searchFilter.add(searchButton);
		
		return searchFilter;
	}
	
	class SelectButtons extends JPanel {

		protected JButton up;
		protected JButton down;

		SelectButtons() {
			up = new JButton("^");
			up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedList.selected.length != 0) {
						for (Object obj : selectedList.selected) {
							selectedList.lingListModel.removeElement(obj);
							list.lingListModel.add(list.pos.get(obj), obj);
						}
					}
				}
			});
			down = new JButton("v");
			down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (list.selected.length != 0) {
						for (Object obj : list.selected) {
							list.lingListModel.removeElement(obj);
							selectedList.lingListModel.addElement(obj);
						}
					}
				}
			});

			add(up);
			add(down);

			setMinimumSize(new Dimension(200, 20));
			setMaximumSize(new Dimension(200, 20));
		}
	}

	class EntityList extends JScrollPane{
		
		protected HashMap<Object, Integer> pos;
		protected Object[] selected;
		protected JXList lingList;
		protected DefaultListModel lingListModel;
		private EntityLibrary library;

		EntityList(EntityLibrary library) {
			lingListModel = new DefaultListModel();
			this.library = library;
			lingList = new JXList(lingListModel);
			setListener();
			this.setViewportView(lingList);
		}

		EntityList(OWLOntology owlModel, EntityLibrary library, String entity) {

			OWLClass cls = library.getClasses().get(entity);
			lingListModel = new DefaultListModel();
			pos = new HashMap<Object, Integer>();
			//System.out.println(cls.getIRI().getFragment());

			for (OWLIndividual ind : cls.getIndividuals(owlModel)) {
				OWLNamedIndividual individual = ind.asOWLNamedIndividual();
				//System.out.println(individual.getIRI().getFragment());
				OWLDataProperty nameProp = library.getDataProperties().get("hasName");
				String name = individual.getDataPropertyValues(nameProp, owlModel).toString();
				//Because Linguistic Metaphors' names include random string, these next few lines will give us the actual name
				String[] arr = name.split("\"");
				name = arr[1];
				SearchPanelListItem item = new SearchPanelListItem(name, individual);
				lingListModel.addElement(item);
				pos.put(item, lingListModel.size() - 1);
			}

			lingList = new JXList(lingListModel);
			setListener();
			this.setViewportView(lingList);

		}

		public void setListener() {
			lingList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false) {
						Object[] selections = lingList.getSelectedValues();
						if (selections.length > 0) {
							selected = selections;
						}
					}
				}
			});
		}


	}
}