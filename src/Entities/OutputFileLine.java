package Entities;

public class OutputFileLine implements  Comparable<OutputFileLine> {
    public String Name;
    public String ClusterName;
    public String ClassName;

    @Override
    public int compareTo(OutputFileLine o) {
        int result = this.ClusterName.compareToIgnoreCase(o.ClusterName);
        if(result != 0){
            return result;
        }else{
            return new String(this.ClusterName).compareTo(new String(o.ClusterName));
        }
    }
}
