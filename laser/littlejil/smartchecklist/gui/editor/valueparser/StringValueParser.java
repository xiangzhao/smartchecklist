package laser.littlejil.smartchecklist.gui.editor.valueparser;


public class StringValueParser implements ValueParser<String> 
{
	public String parse(Class<String> type, String valueString) {
		return valueString;
	}
}