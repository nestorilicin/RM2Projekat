package resenje;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private CpuGraphManager cpuGraphManager;
	private MemGraphManager memoryGraphManager;
	private SNMPCollector collector;

	private List<String> routers = Arrays.asList("R1", "R2", "R3");
	private Map<String, String> routerIPs = new HashMap<>();
	private int intervalSeconds = 5;

	public GraphGUI() {
		routerIPs.put("R1", "192.168.10.1");
		routerIPs.put("R2", "192.168.20.1");
		routerIPs.put("R3", "192.168.30.1");
	}

	public void initialize() {
		promptInterval();
		setTitle("SNMP Monitor");
		setSize(1500, 800);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			collector = new SNMPCollector();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Greška pri inicijalizaciji SNMP: " + e.getMessage());
			System.exit(1);
		}

		cpuGraphManager = new CpuGraphManager(routers);
		memoryGraphManager = new MemGraphManager(routers);

		JPanel cpuPanel = cpuGraphManager.getPanel();
		JPanel memPanel = memoryGraphManager.getPanel();

		JPanel graphsPanel = new JPanel(new GridLayout(2, 1));
		graphsPanel.add(cpuPanel);
		graphsPanel.add(memPanel);

		add(graphsPanel, BorderLayout.CENTER);

		setVisible(true);

		startMonitoring();
	}

	private void promptInterval() {
		while (true) {
			String input = JOptionPane.showInputDialog("Unesite interval u sekundama:");
			if (input == null)
				System.exit(0);
			try {
				int val = Integer.parseInt(input);
				if (val > 0) {
					intervalSeconds = val;
					break;
				}
			} catch (NumberFormatException ignored) {
				System.exit(2);
			}
		}
	}

	private void startMonitoring() {
		new Thread(() -> {
			while (true) {
				for (String router : routers) {
					String ip = routerIPs.get(router);

					long[] cpu = collector.getCPU(ip);
					cpuGraphManager.addCpuData(router, cpu);

					List<MemoryPoolInfo> mem = collector.getMemoryPools(ip);
					memoryGraphManager.addMemoryData(router, mem);
				}

				try {
					Thread.sleep(intervalSeconds * 1000);
				} catch (InterruptedException e) {
					System.err.println("Monitoring prekinut.");
					return;
				}
			}
		}).start();
	}
}
