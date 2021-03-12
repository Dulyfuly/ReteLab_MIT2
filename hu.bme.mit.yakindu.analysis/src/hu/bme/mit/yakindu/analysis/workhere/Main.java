package hu.bme.mit.yakindu.analysis.workhere;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		List<State> csapdak = new LinkedList<State>();
		List<State> nevtelenek = new LinkedList<State>();
		List<VariableDefinition> belsoValtozok = new LinkedList<VariableDefinition>();
		List<EventDefinition> bemenoEsemenyek = new LinkedList<EventDefinition>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				String actual = state.getName();
				System.out.println(actual);
				if(state.getOutgoingTransitions().isEmpty()) csapdak.add(state);
				if(state.getName() == null || state.getName().contentEquals(""))
					nevtelenek.add(state);
			}
			else if(content instanceof Transition) {
				Transition tr = (Transition) content;
				System.out.println(tr.getSource().getName()
						+ " -> " + tr.getTarget().getName());
			}
			else if(content instanceof VariableDefinition) {
				belsoValtozok.add((VariableDefinition) content);
			}
			else if(content instanceof EventDefinition) {
				bemenoEsemenyek.add((EventDefinition) content);
			}
		}
		System.out.println("Csapda allapotok:");
		for (State state : csapdak) {
			System.out.println(state.getName());
		}
		System.out.println("Nevtelen allapotok:");
		for (State state : nevtelenek) {
			int id = state.hashCode();
			keres: while (true) {
				for (State anotherState : nevtelenek) {
					if(anotherState.getName().contentEquals("S" + id)) {
						nevtelenek.add(state);
						id++;
						continue keres;
					}
				}
				break;
			}
			state.setName("S" + id);
			System.out.println("S" + id);
		}
		System.out.println("Belso valtozok:");
		for (VariableDefinition v : belsoValtozok) {
			System.out.println(v.getName());
		}
		System.out.println("Bemeno esemenyek");
		for (EventDefinition e : bemenoEsemenyek) {
			System.out.println(e.getName());
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
