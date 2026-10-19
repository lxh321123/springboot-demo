package trash;

public class Factorial {
    public static void main(String[] args) {
        int []array = {5, 2, 1, 3, 4};
        int max = -1;
        for (int num : array) {
            max = Math.max(max, num);
        }
        System.out.println(max);
    }
}
