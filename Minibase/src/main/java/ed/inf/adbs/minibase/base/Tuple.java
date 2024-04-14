package ed.inf.adbs.minibase.base;

/**
 * Tuple class which made out of List of String Datatype
 * Has getValue which gives value at that particular index
 * Has getValues which gives all values of tuple
 * Has setValues which sets all values of tuple
 * Has size which gives size of tuple
 * Has printuple which displays tuple on screen
 */

import java.util.List;
public class Tuple {
    private List<String> values;

    public Tuple(List<String> values) {
        this.values = values;
    }

    public String getValue(int index) {
//        int index = index;
        return values.get(index);
    }

    public List getValues() {
//        int index = index;
        return this.values;
    }

    public void setValues(List<String> newValues) {
        this.values.clear();
        this.values.addAll(newValues);
    }




    public int size() {
//        int index = index;
        return values.size();
    }

    public String printTuple()
    {
        int len = values.size();
        String tuple="(";
        int i=0;
        while(i<len){
            tuple+=this.getValue(i);
            if(i<len-1){
                tuple+=",";
            }
            i+=1;
        }
        tuple+=")";
        return tuple;
    }


}
//public class Tuple<T,U> implements Serializable {
////    private static final long serialVersionUID = -2344986941139471507L;
//
//    private List<Object> values;
//
//    public Tuple(List<Object> values) {
//        this.values = values;
//    }
//
//    public Object getValue(int index) {
//        return values.get(index);
//    }
//
//
//
//    private final T val1;
//    private final U val2;
//
//    public Tuple(final T val1, final U val2) {
//        this.val1 = val1;
//        this.val2 = val2;
//    }
//
//    public static <T,U> Tuple of(final T val1, final U val2){
//        return new Tuple(val1,val2);
//    }
//
//    public T getVal1() {
//        return val1;
//    }
//
//    public U getVal2() {
//        return val2;
//    }
//
//    @Override
//    public String toString() {
//        return "Pair{" +
//                "val1=" + val1 +
//                ", val2=" + val2 +
//                '}';
//    }
//}
