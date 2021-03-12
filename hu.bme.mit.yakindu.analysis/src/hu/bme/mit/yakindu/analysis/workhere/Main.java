package hu.bme.mit.yakindu.analysis.workhere;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
		//List<State> csapdak = new LinkedList<State>();
		//List<State> nevtelenek = new LinkedList<State>();
		List<VariableDefinition> belsoValtozok = new LinkedList<VariableDefinition>();
		List<EventDefinition> bemenoEsemenyek = new LinkedList<EventDefinition>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			/*if(content instanceof State) {
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
			else*/ if(content instanceof VariableDefinition) {
				belsoValtozok.add((VariableDefinition) content);
			}
			else if(content instanceof EventDefinition) {
				bemenoEsemenyek.add((EventDefinition) content);
			}
		}
		/*System.out.println("Csapda allapotok:");
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
		}*/
		System.out.println("package hu.bme.mit.yakindu.analysis.workhere;\r\n" + 
				"\r\n" + 
				"import java.io.IOException;\r\n" + 
				"import java.util.Scanner;\r\n" + 
				"\r\n" + 
				"import hu.bme.mit.yakindu.analysis.RuntimeService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.TimerService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"public class RunStatechart {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" +
				"		Scanner scanner = new Scanner(System.in);\r\n" + 
				"		while(true) {\r\n" + 
				"			switch (scanner.next()) {");
				for (EventDefinition e : bemenoEsemenyek) {
					String nagyKezdobetu = e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);
					System.out.println(
				"			case \""+e.getName()+"\":\r\n" + 
				"				s.raise"+nagyKezdobetu+"();\r\n" + 
				"				s.runCycle();\r\n" + 
				"				break;");
				}
				System.out.println(
				"			case \"exit\":\r\n" + 
				"				System.exit(0);\r\n" + 
				"				break;\r\n" + 
				"			}\r\n" + 
				"			print(s);\r\n" + 
				"		}\r\n" + 
				"	}\r\n\r\n" + 
				"	public static void print(IExampleStatemachine s) {");
		for (VariableDefinition v : belsoValtozok) {
			String nagyKezdobetu = v.getName().substring(0, 1).toUpperCase() + v.getName().substring(1);
			System.out.println("		System.out.println(\""+v.getName()+" = \" + s.getSCInterface().get"+nagyKezdobetu+"());");
		}
		System.out.println("	}\r\n}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
