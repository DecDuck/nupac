public class Util {

    public static String GetVar(String name){
        for(Variable v:Master.variables){
            if(v.name.equals(name)){
                return v.value;
            }
        }
        return name.replace(";", "");
    }

    public static void CreateVar(String name, String value, boolean set){
        if(name == ";"){
            return;
        }
        for(Variable v:Master.variables){
            if(v.name.equals(name) && set){
                v.value = value;
                return;
            }
        }
        if(set){
            return;
        }
        Master.variables.add(new Variable(name, value));
    }

}
