/*
 * generated by Xtext 2.17.0
 */
package org.icyphy.serializer;

import com.google.inject.Inject;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;
import org.icyphy.linguaFranca.Assignment;
import org.icyphy.linguaFranca.Assignments;
import org.icyphy.linguaFranca.ComponentBody;
import org.icyphy.linguaFranca.Composite;
import org.icyphy.linguaFranca.Connection;
import org.icyphy.linguaFranca.Gets;
import org.icyphy.linguaFranca.Import;
import org.icyphy.linguaFranca.Input;
import org.icyphy.linguaFranca.Instance;
import org.icyphy.linguaFranca.LinguaFrancaPackage;
import org.icyphy.linguaFranca.Model;
import org.icyphy.linguaFranca.Output;
import org.icyphy.linguaFranca.Param;
import org.icyphy.linguaFranca.Params;
import org.icyphy.linguaFranca.Preamble;
import org.icyphy.linguaFranca.Reaction;
import org.icyphy.linguaFranca.Reactor;
import org.icyphy.linguaFranca.Sets;
import org.icyphy.linguaFranca.Target;
import org.icyphy.linguaFranca.Timer;
import org.icyphy.linguaFranca.Timing;
import org.icyphy.services.LinguaFrancaGrammarAccess;

@SuppressWarnings("all")
public class LinguaFrancaSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private LinguaFrancaGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == LinguaFrancaPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case LinguaFrancaPackage.ACTION:
				sequence_Action(context, (org.icyphy.linguaFranca.Action) semanticObject); 
				return; 
			case LinguaFrancaPackage.ASSIGNMENT:
				sequence_Assignment(context, (Assignment) semanticObject); 
				return; 
			case LinguaFrancaPackage.ASSIGNMENTS:
				sequence_Assignments(context, (Assignments) semanticObject); 
				return; 
			case LinguaFrancaPackage.COMPONENT_BODY:
				sequence_ComponentBody(context, (ComponentBody) semanticObject); 
				return; 
			case LinguaFrancaPackage.COMPOSITE:
				sequence_Composite(context, (Composite) semanticObject); 
				return; 
			case LinguaFrancaPackage.CONNECTION:
				sequence_Connection(context, (Connection) semanticObject); 
				return; 
			case LinguaFrancaPackage.GETS:
				sequence_Gets(context, (Gets) semanticObject); 
				return; 
			case LinguaFrancaPackage.IMPORT:
				sequence_Import(context, (Import) semanticObject); 
				return; 
			case LinguaFrancaPackage.INPUT:
				sequence_Input(context, (Input) semanticObject); 
				return; 
			case LinguaFrancaPackage.INSTANCE:
				sequence_Instance(context, (Instance) semanticObject); 
				return; 
			case LinguaFrancaPackage.MODEL:
				sequence_Model(context, (Model) semanticObject); 
				return; 
			case LinguaFrancaPackage.OUTPUT:
				sequence_Output(context, (Output) semanticObject); 
				return; 
			case LinguaFrancaPackage.PARAM:
				sequence_Param(context, (Param) semanticObject); 
				return; 
			case LinguaFrancaPackage.PARAMS:
				sequence_Params(context, (Params) semanticObject); 
				return; 
			case LinguaFrancaPackage.PREAMBLE:
				sequence_Preamble(context, (Preamble) semanticObject); 
				return; 
			case LinguaFrancaPackage.REACTION:
				sequence_Reaction(context, (Reaction) semanticObject); 
				return; 
			case LinguaFrancaPackage.REACTOR:
				sequence_Reactor(context, (Reactor) semanticObject); 
				return; 
			case LinguaFrancaPackage.SETS:
				sequence_Sets(context, (Sets) semanticObject); 
				return; 
			case LinguaFrancaPackage.TARGET:
				sequence_Target(context, (Target) semanticObject); 
				return; 
			case LinguaFrancaPackage.TIMER:
				sequence_Timer(context, (Timer) semanticObject); 
				return; 
			case LinguaFrancaPackage.TIMING:
				sequence_Timing(context, (Timing) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     Action returns Action
	 *
	 * Constraint:
	 *     ((name=ID | name='action') timing=Timing?)
	 */
	protected void sequence_Action(ISerializationContext context, org.icyphy.linguaFranca.Action semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Assignment returns Assignment
	 *
	 * Constraint:
	 *     (name=ID value=Value)
	 */
	protected void sequence_Assignment(ISerializationContext context, Assignment semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.ASSIGNMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.ASSIGNMENT__NAME));
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.ASSIGNMENT__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.ASSIGNMENT__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAssignmentAccess().getNameIDTerminalRuleCall_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getAssignmentAccess().getValueValueParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Assignments returns Assignments
	 *
	 * Constraint:
	 *     (assignments+=Assignment assignments+=Assignment*)
	 */
	protected void sequence_Assignments(ISerializationContext context, Assignments semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     ComponentBody returns ComponentBody
	 *
	 * Constraint:
	 *     (
	 *         name=ID 
	 *         parameters=Params? 
	 *         inputs+=Input* 
	 *         outputs+=Output* 
	 *         (timers+=Timer | actions+=Action)* 
	 *         preamble=Preamble? 
	 *         reactions+=Reaction*
	 *     )
	 */
	protected void sequence_ComponentBody(ISerializationContext context, ComponentBody semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Component returns Composite
	 *     Composite returns Composite
	 *
	 * Constraint:
	 *     (componentBody=ComponentBody instances+=Instance* connections+=Connection*)
	 */
	protected void sequence_Composite(ISerializationContext context, Composite semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Connection returns Connection
	 *
	 * Constraint:
	 *     (leftPort=Port rightPort=Port)
	 */
	protected void sequence_Connection(ISerializationContext context, Connection semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.CONNECTION__LEFT_PORT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.CONNECTION__LEFT_PORT));
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.CONNECTION__RIGHT_PORT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.CONNECTION__RIGHT_PORT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getConnectionAccess().getLeftPortPortParserRuleCall_0_0(), semanticObject.getLeftPort());
		feeder.accept(grammarAccess.getConnectionAccess().getRightPortPortParserRuleCall_2_0(), semanticObject.getRightPort());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Gets returns Gets
	 *
	 * Constraint:
	 *     (gets+=ID gets+=ID?)
	 */
	protected void sequence_Gets(ISerializationContext context, Gets semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Import returns Import
	 *
	 * Constraint:
	 *     name=Path
	 */
	protected void sequence_Import(ISerializationContext context, Import semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.IMPORT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.IMPORT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getImportAccess().getNamePathParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Input returns Input
	 *
	 * Constraint:
	 *     ((name=ID | name='input') type=Type?)
	 */
	protected void sequence_Input(ISerializationContext context, Input semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Instance returns Instance
	 *
	 * Constraint:
	 *     (name=ID actorClass=ID parameters=Assignments?)
	 */
	protected void sequence_Instance(ISerializationContext context, Instance semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Model returns Model
	 *
	 * Constraint:
	 *     (target=Target imports+=Import* components+=Component+)
	 */
	protected void sequence_Model(ISerializationContext context, Model semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Output returns Output
	 *
	 * Constraint:
	 *     ((name=ID | name='output') type=Type?)
	 */
	protected void sequence_Output(ISerializationContext context, Output semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Param returns Param
	 *
	 * Constraint:
	 *     (name=ID type=Type? value=Value?)
	 */
	protected void sequence_Param(ISerializationContext context, Param semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Params returns Params
	 *
	 * Constraint:
	 *     (params+=Param params+=Param*)
	 */
	protected void sequence_Params(ISerializationContext context, Params semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Preamble returns Preamble
	 *
	 * Constraint:
	 *     code=CODE
	 */
	protected void sequence_Preamble(ISerializationContext context, Preamble semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.PREAMBLE__CODE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.PREAMBLE__CODE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getPreambleAccess().getCodeCODETerminalRuleCall_1_0(), semanticObject.getCode());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Reaction returns Reaction
	 *
	 * Constraint:
	 *     ((triggers+=ID triggers+=ID*)? gets=Gets? sets=Sets? code=CODE)
	 */
	protected void sequence_Reaction(ISerializationContext context, Reaction semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Component returns Reactor
	 *     Reactor returns Reactor
	 *
	 * Constraint:
	 *     componentBody=ComponentBody
	 */
	protected void sequence_Reactor(ISerializationContext context, Reactor semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.COMPONENT__COMPONENT_BODY) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.COMPONENT__COMPONENT_BODY));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getReactorAccess().getComponentBodyComponentBodyParserRuleCall_1_0(), semanticObject.getComponentBody());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Sets returns Sets
	 *
	 * Constraint:
	 *     (sets+=ID sets+=ID?)
	 */
	protected void sequence_Sets(ISerializationContext context, Sets semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Target returns Target
	 *
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_Target(ISerializationContext context, Target semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, LinguaFrancaPackage.Literals.TARGET__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, LinguaFrancaPackage.Literals.TARGET__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getTargetAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Timer returns Timer
	 *
	 * Constraint:
	 *     ((name=ID | name='timer') timing=Timing?)
	 */
	protected void sequence_Timer(ISerializationContext context, Timer semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Timing returns Timing
	 *
	 * Constraint:
	 *     ((offset='NOW' | offset=ID | offset=NUMBER) (period='ONCE' | period='STOP' | period=ID | period=NUMBER)?)
	 */
	protected void sequence_Timing(ISerializationContext context, Timing semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
}
