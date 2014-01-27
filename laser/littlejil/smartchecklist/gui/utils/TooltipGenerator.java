package laser.littlejil.smartchecklist.gui.utils;

import java.io.Serializable;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.Activity;
import laser.littlejil.smartchecklist.gui.model.ParameterDeclaration;


public class TooltipGenerator 
{
	private static final String FIFTEEN_SPACES = "               ";
	public static final String REQUIRES_TITLE = "INPUTS";
	public static final String FINISHED_SUCCESSFULLY_TITLE = "OUTPUTS";
	public static final String FINISHED_SUCCESSFULLY_TITLE_FOR_PROCESS_HEADER = "PROCESS COMPLETED SUCCESSFULLY";
	public static final String FINISHED_FAILED_TITLE = "PROBLEMS";
	public static final String FINISHED_FAILED_TITLE_FOR_PROCESS_HEADER = "THE FOLLOWING PROBLEMS AROSE WHILE PERFORMING THE PROCESS";
	public static final String SEPARATOR = ":";
	public static final String BULLET = "* ";
	public static final String LS = System.getProperty("line.separator", "\n");
	
	
	public static String generateParameterDeclsTooltip(Activity activity, String title, Set<ParameterDeclaration> parameterDeclSet) {
		String parameterDeclsTooltip = title + SEPARATOR + FIFTEEN_SPACES + LS;
		
		for (ParameterDeclaration paramFormal : parameterDeclSet)
		{
//				System.out.println("Working on paramFormal " + paramFormal.getName());
			Serializable paramActual = activity.getParameterValue(paramFormal.getName());
//				System.out.println("paramActual = " + paramActual);
			parameterDeclsTooltip += LS + BULLET + PrettyPrintedNamesFormatter.createHumanReadableName(paramFormal.getName());
			if (paramActual != null) { 
				parameterDeclsTooltip += SEPARATOR + "\t " + paramActual.toString();
			}
//			// Double spacing
//			parameterDeclsTooltip += LS;
		}
		
		return parameterDeclsTooltip;
	}

	public static String generateProblemsTooltip(Set<Serializable> thrownExceptions) {
		String problemsTooltip = FINISHED_FAILED_TITLE + SEPARATOR + LS;
		
		for (Serializable thrownException : thrownExceptions)
		{
			//System.out.println("Working on thrownException " + thrownException.getClass().getName());
			problemsTooltip += LS + BULLET + PrettyPrintedNamesFormatter.createHumanReadableName(thrownException.getClass().getName());
//			// Double spacing
//			problemsTooltip += LS;
		}
		
		return problemsTooltip;
	}
	
	public static String generateProblemsTooltipForProcessHeader(Set<Serializable> thrownExceptions)
	{
		String problemsTooltip = FINISHED_FAILED_TITLE_FOR_PROCESS_HEADER + SEPARATOR + LS;
		
		for (Serializable thrownException : thrownExceptions)
		{
			problemsTooltip += LS + BULLET + PrettyPrintedNamesFormatter.createHumanReadableName(thrownException.getClass().getName());
		}
		
		return problemsTooltip;
	}
	
	public static String generateProcessCompletedSuccessfullyTooltip()
	{
		return FINISHED_SUCCESSFULLY_TITLE_FOR_PROCESS_HEADER;
	}
}
