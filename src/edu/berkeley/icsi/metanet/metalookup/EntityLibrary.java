package edu.berkeley.icsi.metanet.metalookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class EntityLibrary {
	
	private OWLOntology owlModel;
	private HashMap<String, OWLClass> classes;
	private HashMap<String, OWLNamedIndividual> individuals;
	private HashMap<String, OWLNamedIndividual> individualsByName;
	private HashMap<String, OWLObjectProperty> objectProperties;
	private HashMap<String, OWLDataProperty> dataProperties;
	private HashMap<String, Set<OWLNamedIndividual>> subjects;
	private HashMap<String, OWLAnnotationProperty> annotations;
	
	public EntityLibrary(OWLOntology owlModel) {
		
		this.owlModel = owlModel;
		classes = new HashMap<String, OWLClass>();
		individuals = new HashMap<String, OWLNamedIndividual>();
		objectProperties = new HashMap<String, OWLObjectProperty>();
		dataProperties = new HashMap<String, OWLDataProperty>();
		subjects = new HashMap<String, Set<OWLNamedIndividual>>();
		annotations = new HashMap<String, OWLAnnotationProperty>();
		individualsByName = new HashMap<String, OWLNamedIndividual>();
		
		for (OWLAnnotationProperty prop : owlModel.getAnnotationPropertiesInSignature()) {
			annotations.put(prop.getIRI().getFragment(), prop);
		}
		for (OWLClass cls : owlModel.getClassesInSignature(true)) {
			classes.put(cls.getIRI().getFragment(), cls);
		}
		for (OWLNamedIndividual ind : owlModel.getIndividualsInSignature(true)) {
			individuals.put(ind.getIRI().getFragment(), ind);
		}
		for (OWLObjectProperty prop: owlModel.getObjectPropertiesInSignature(true)) {
			objectProperties.put(prop.getIRI().getFragment(), prop);
		}
		for (OWLDataProperty prop: owlModel.getDataPropertiesInSignature(true)) {
			dataProperties.put(prop.getIRI().getFragment(), prop);
		}
		for (OWLNamedIndividual ind : owlModel.getIndividualsInSignature(true)) {
			OWLAnnotationProperty anno = annotations.get("label");
			//System.out.println(anno);
			//if (ind.getAnnotations(owlModel, anno) != null) {
				individualsByName.put(stripChar(ind.getAnnotations(owlModel, anno).toString()), ind);
			//}
		}
		
		/* HashMap for Subject, Property, Object to quickly lookup subjects for a certain pair of Object & Property using OWL's Assertion Axioms
		 * key is the concat of getIRI().getFragment() for the Property and Object in that order
		 * value is a Set of Subjects
		 * Subject & Object are OWLNamedIndividuals
		 * Property is OWLObjectProperty
		 */
		Set<OWLObjectPropertyAssertionAxiom> axes = owlModel.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, true);
		for (OWLObjectPropertyAssertionAxiom ax : axes) {
			OWLNamedIndividual obj = ax.getObject().asOWLNamedIndividual();
			OWLObjectProperty prop = ax.getProperty().asOWLObjectProperty();
			String hashKey = prop.getIRI().getFragment() + obj.getIRI().getFragment();
			if (subjects.containsKey(hashKey)) {
				subjects.get(hashKey).add(ax.getSubject().asOWLNamedIndividual());
			} else {
				Set<OWLNamedIndividual> indies = new HashSet<OWLNamedIndividual>();
				indies.add(ax.getSubject().asOWLNamedIndividual());
				subjects.put(hashKey, indies);
			}
		}
	}
	
	public HashMap<String, OWLAnnotationProperty> getAnnotations() {
		return annotations;
	}
	
	public HashMap<String, OWLClass> getClasses() {
		return classes;
	}
	
	public HashMap<String, OWLNamedIndividual> getIndividuals() {
		return individuals;
	}
	
	public HashMap<String, OWLNamedIndividual> getNameIndividuals() {
		return individualsByName;
	}
	
	public HashMap<String, OWLObjectProperty> getObjectProperties() {
		return objectProperties;
	}
	
	public HashMap<String, OWLDataProperty> getDataProperties() {
		return dataProperties;
	}
	
	public HashMap<String, Set<OWLNamedIndividual>> getSubjectsSet() {
		return subjects;
	}
	
	public HashMap<String, OWLNamedIndividual> indieHashSet(OWLNamedIndividual individual, OWLObjectProperty property) {
		HashMap<String, OWLNamedIndividual> mySet = new HashMap<String, OWLNamedIndividual>();
		Set<OWLIndividual> indSet = individual.getObjectPropertyValues(property, owlModel);
		for (OWLIndividual ind : indSet) {
			OWLNamedIndividual indie = ind.asOWLNamedIndividual();
			mySet.put(indie.getIRI().getFragment(), indie);
		}
		return mySet;
	}
	
	private String stripChar(String raw) {
		//Because Linguistic Metaphors' names include random string, these next few lines will give us the actual name
		String[] arr = raw.split("\"");
		raw = arr[1];
		
		return raw;
	}
	
}

