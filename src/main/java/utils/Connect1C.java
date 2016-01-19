package utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
/**
 * Created by Igor Klekotnev on 14.01.2016.
 */
public class Connect1C {
    /*
    ActiveXComponent v8App = new ActiveXComponent(prop.getProperty("v8.Application"));
    String conString = "File=Path;Usr=Admin;Pwd=Admin";
    Variant connected = Dispatch.call(v8, "Connect", conString);

    Dispatch query = Dispatch.call(v8App , "NewObject", "Query").getDispatch();

    public void request1C () {
        Dispatch.put(query, "Text", "SELECT Items.Ref, Items.Description FROM Справочник.Номенклатура AS Items");
        Dispatch result = Dispatch.call(query, "Execute").toDispatch();
        Dispatch select = Dispatch.call(result, "Choose").toDispatch();
        while (Dispatch.call(select, "Next").getBoolean())
        {
            Dispatch.get(select, "Ref").getDispatch();
        }
    }
    */
}
