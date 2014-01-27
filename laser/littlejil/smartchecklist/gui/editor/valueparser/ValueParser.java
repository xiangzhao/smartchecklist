package laser.littlejil.smartchecklist.gui.editor.valueparser;


public interface ValueParser <T> 
{
	public T parse(Class<T> type, String valueString);
}

