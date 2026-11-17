
package homework;

public class Main {

    public static void main(String[] args) {

        // Linear Probing + SSF
        HashTable table = new HashTable(11, 0.75, false, false);

        // Put işlemleri
        table.put("Ali", new DocumentFrequency("file1.txt", 1));
        table.put("Ayse", new DocumentFrequency("file1.txt", 1));
        table.put("Mehmet", new DocumentFrequency("file1.txt", 1));
        table.put("Zeynep", new DocumentFrequency("file1.txt", 1));

        // Tekrar ekleme testi
        table.put("Ali", new DocumentFrequency("file1.txt", 1));

        System.out.println("===== HASH TABLE TEST =====");

        // Get işlemleri
        String[] words = {
            "Ali", "Ayse", "Mehmet", "Zeynep", "Ahmet"
        };

        for (String word : words) {

            Object result = table.get(word);

            if (result != null) {
                System.out.println(word + " bulundu: " + result);
            } else {
                System.out.println(word + " bulunamadi.");
            }
        }

        // Collision sayısı
        System.out.println("\nCollision Sayisi: "
                + table.getCollisionCount());

        System.out.println("\n===== TEST TAMAMLANDI =====");
    }
}
