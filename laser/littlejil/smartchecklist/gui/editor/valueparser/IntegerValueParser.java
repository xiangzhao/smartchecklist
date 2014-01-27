package laser.littlejil.smartchecklist.gui.editor.valueparser;


public class IntegerValueParser implements ValueParser<Integer> 
{
	public Integer parse(Class<Integer> type, String valueString) {
		return Integer.parseInt(valueString);
	}
}
	
	