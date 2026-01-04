public class mergeSortedArray {
    static int[] merger(int[]a,int[]b) {
    int[] merged=new int[a.length+b.length];
    int i=0,j=0,k=0;
    while(i<a.length && j<b.length){
        if(a[i]<=b[j]){
            merged[k++]=a[i++];
        }
        else{
            merged[k++]=b[j++];
        }
    }
    while(i<a.length){
        merged[k++]=a[i++];
    }
    while(j<b.length){
        merged[k++]=b[j++];
    }
    return merged;
    }
    public static void main(String[] args) {
        int[] a={1,2,5,6,15};
        int[] b={1,6,7,10};
        int[]merged=merger(a,b);
        for(int i:merged){
            System.out.println(i);
        }
    }
}
