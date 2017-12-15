package edu.berkeley.icsi.metanet.metalookup;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class SearchPanelListItem {

	private String displayName;
	private OWLNamedIndividual individual;
	private OWLObjectProperty property;
	
	public SearchPanelListItem(String displayName, OWLObjectProperty property) {
		this.displayName = displayName;
		this.property = property;
		individual = null;
	}
	
	public SearchPanelListItem(String displayName, OWLNamedIndividual individual) {
		this.displayName = displayName;
		this.individual = individual;
		property = null;
	}
	
	public OWLNamedIndividual individual() {
		return individual;
	}
	
	public OWLObjectProperty property() {
		return property;
	}
	
	public String toString() {
		return displayName;
	}
	
}
