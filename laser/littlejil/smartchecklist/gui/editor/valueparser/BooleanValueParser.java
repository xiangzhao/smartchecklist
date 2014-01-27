package laser.littlejil.smartchecklist.gui.editor.valueparser;


public class BooleanValueParser implements ValueParser<Boolean> 
{
	public Boolean parse(Class<Boolean> type, String valueString) {
		return Boolean.parseBoolean(valueString);
	}
}
	
	