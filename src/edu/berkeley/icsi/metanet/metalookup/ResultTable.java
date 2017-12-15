package edu.berkeley.icsi.metanet.metalookup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class ResultTable extends JScrollPane {
	
	private OWLOntology owlModel;
	private EntityLibrary library;
	private SearchPanelListItem[] items;
	private Object[][] results;
	
	ResultTable() {
		
		owlModel = null;
		library = null;
		items = null;
		results = null;
		
		String[] tableHeaders = {"Linguistic Metaphor",
										 "Source Lexical Units",
										 "Target Lexical Units",
										 "Source Schemas",
										 "Target Schemas",
										 "Metaphors",
										 "Source Frame",
										 "Target Frame" };
		
		Object[][] data = {
			    {"", "", "", "", "", "", "", ""}
			};
		
		JTable resultTable = new JTable(data, tableHeaders);
		
		add(resultTable);
		resultTable.setFillsViewportHeight(true);
		setViewportView(resultTable);
		
	}
	
	ResultTable(OWLOntology owlModel, SearchPanelListItem[] items, EntityLibrary library) {
		
		this.owlModel = owlModel;
		this.library = library;
		this.items = items;
		runSearch();
		
		String[] tableHeaders = {"Linguistic Metaphor",
				 "Source Lexical Units",
				 "Target Lexical Units",
				 "Source Schemas",
				 "Target Schemas",
				 "Metaphors",
				 "Source Frame",
				 "Target Frame" };
		
		JTable resultTable = new JTable(results, tableHeaders);
		TableColumn column = null;
		for (int i = 0; i < 8; i++) {
		    column = resultTable.getColumnModel().getColumn(i);
		    column.setPreferredWidth(150);
		}
		resultTable.setFillsViewportHeight(true);
		resultTable.setAutoCreateRowSorter(true);
		resultTable.setEnabled(false);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resultTable.setShowGrid(true);
		resultTable.setShowVerticalLines(false);
		resultTable.setShowHorizontalLines(true);
		resultTable.setGridColor(Color.GRAY);
		resultTable.setAutoscrolls(true);
		
		add(resultTable);
		setViewportView(resultTable);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
	}
	
	private void runSearch() {
		
		OWLDataProperty hasLinguisticTarget = library.getDataProperties().get("hasLinguisticTarget");
		OWLDataProperty hasLinguisticSource = library.getDataProperties().get("hasLinguisticSource");
		OWLAnnotationProperty label = library.getAnnotations().get("label");
		OWLDataProperty hasName = library.getDataProperties().get("hasName"); // name for most Entities
		OWLDataProperty hasLemma = library.getDataProperties().get("hasLemma"); // name for LexicalUnits
		OWLObjectProperty hasLexicalUnit = library.getObjectProperties().get("hasLexicalUnit");
		OWLObjectProperty hasSourceSchema = library.getObjectProperties().get("hasSourceSchema");
		OWLObjectProperty hasTargetSchema = library.getObjectProperties().get("hasTargetSchema");
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		
		for ( SearchPanelListItem item : items ) {
			Object[] args = new Object[8];
			for (int i = 0; i < 8; i++) {
				args[i] = "";
			}
			OWLNamedIndividual linguisticMetaphor = item.individual();
			String targetLexUnit = stripQuotations(linguisticMetaphor.getDataPropertyValues(hasLinguisticTarget, owlModel).toString());
			String sourceLexUnit = stripQuotations(linguisticMetaphor.getDataPropertyValues(hasLinguisticSource, owlModel).toString());
			//System.out.println(targetLexUnit + " " + sourceLexUnit + " " + hasLexicalUnit.getIRI().getFragment());
			OWLNamedIndividual sourceLex = library.getNameIndividuals().get(sourceLexUnit);
			OWLNamedIndividual targetLex = library.getNameIndividuals().get(targetLexUnit);
			if (sourceLex != null && targetLex != null) {
				//System.out.println("HEY I GOT HEREJSHFKLJSHELKJFHEWR " + sourceLexUnit + " " + targetLexUnit + " " + item.toString());
				String sourceHashKey = hasLexicalUnit.getIRI().getFragment() + sourceLex.getIRI().getFragment();
				String targetHashKey = hasLexicalUnit.getIRI().getFragment() + targetLex.getIRI().getFragment();
				Set<OWLNamedIndividual> sourceSchemas = library.getSubjectsSet().get(sourceHashKey);
				Set<OWLNamedIndividual> targetSchemas = library.getSubjectsSet().get(targetHashKey);

				for (OWLNamedIndividual source : sourceSchemas) {
					args[3] = args[3] + stripQuotations(source.getAnnotations(owlModel, label).toString()) + "\n";
					String potentialHashKey = hasSourceSchema.getIRI().getFragment() + source.getIRI().getFragment();
					Set<OWLNamedIndividual> potentialMetaphors = library.getSubjectsSet().get(potentialHashKey);
					if (potentialMetaphors != null) {
						for (OWLNamedIndividual potentialMetaphor : potentialMetaphors) {
							for (OWLNamedIndividual target : targetSchemas) {
								HashMap<String, OWLNamedIndividual> potentialTargets = library.indieHashSet(potentialMetaphor, hasTargetSchema);
								if (potentialTargets != null && potentialTargets.containsKey(target.getIRI().getFragment())) {
									args[5] = args[5] + stripQuotations(potentialMetaphor.getAnnotations(owlModel, label).toString()) + "\n";
								}
							}
						}
					}
				}

				for (OWLNamedIndividual target : targetSchemas) {
					args[4] = args[4] + stripQuotations(target.getAnnotations(owlModel, label).toString()) + "\n";
				}

				args[6] = "";
				args[7] = "";
			}
			args[0] = item.toString();
			args[1] = sourceLexUnit;
			args[2] = targetLexUnit;
			data.add(args);
		}
		
		Object[][] results = new Object[data.size()][8];
		//Object[] inner = data.toArray();
		for (int i = 0; i < data.size(); i++) {
			results[i] = data.get(i);
		}
		
		this.results = results;
		
	}
	
	private String stripQuotations(String raw) {
		//Because Linguistic Metaphors' names include random string, these next few lines will give us the actual name
		String[] arr = raw.split("\"");
		raw = arr[1];
		
		return raw;
	}
}