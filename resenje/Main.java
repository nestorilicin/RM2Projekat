package resenje;

public class Main {
	public static void main(String[] args) {
		new GraphGUI().initialize();
	}
}
/* Komande za setap ruting tabele (iz teksta projekta)
 * sudo ip route add 192.168.10.0/24 via 192.168.122.100 dev virbr0
 * sudo ip route add 192.168.20.0/24 via 192.168.122.100 dev virbr0
 * sudo ip route add 192.168.30.0/24 via 192.168.122.100 dev virbr0
 * sudo ip route add 192.168.12.0/24 via 192.168.122.100 dev virbr0
 * sudo ip route add 192.168.13.0/24 via 192.168.122.100 dev virbr0
 * sudo ip route add 192.168.23.0/24 via 192.168.122.100 dev virbr0*/
