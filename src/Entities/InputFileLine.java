package Entities;

public class InputFileLine implements  Comparable<InputFileLine> {
    public String Name;
    public String ChildLib;
    public String ParentLib;
    public String ClusterName;
    public boolean IsClustered;

    @Override
    public int compareTo(InputFileLine f) {
        int result = this.ClusterName.compareToIgnoreCase(f.ClusterName);
        if(result != 0){
            return result;
        }else{
            return new String(this.ClusterName).compareTo(new String(f.ClusterName));
        }
    }


}
