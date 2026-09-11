package resenje;

import java.util.*;
import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.snmp4j.CommunityTarget;

public class SNMPCollector {
	private Snmp snmp;
	private String community = "si2019";
	private int version = SnmpConstants.version2c;

	public static final int PORT = 161;

	public SNMPCollector() {
		try {
			TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CommunityTarget createTarget(String ip) {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(new UdpAddress(ip + "/" + PORT));
		target.setVersion(version);
		target.setTimeout(1500);
		target.setRetries(2);
		return target;
	}

	public long[] getCPU(String ip) {
		long[] result = new long[3];
		CommunityTarget target = createTarget(ip);
		OID[] columns = new OID[] { new OID("1.3.6.1.4.1.9.9.109.1.1.1.1.3"), // 1s
				new OID("1.3.6.1.4.1.9.9.109.1.1.1.1.4"), // 1min
				new OID("1.3.6.1.4.1.9.9.109.1.1.1.1.5") // 5min
		};

		TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
		List<TableEvent> events = tableUtils.getTable(target, columns, null, null);

		for (TableEvent event : events) {
			if (event == null || event.getColumns() == null || event.isError())
				continue;
			VariableBinding[] vb = event.getColumns();
			long sek5 = vb[0].getVariable().toLong();
			long min1 = vb[1].getVariable().toLong();
			long min5 = vb[2].getVariable().toLong();
			result[0] = sek5;
			result[1] = min1;
			result[2] = min5;
		}
		// System.out.println(result[0] + " " + result[1] + " "+result[2] );
		return result;
	}

	public List<MemoryPoolInfo> getMemoryPools(String ip) {
		List<MemoryPoolInfo> result = new ArrayList<MemoryPoolInfo>();
		CommunityTarget target = createTarget(ip);
		OID[] columns = new OID[] { new OID("1.3.6.1.4.1.9.9.48.1.1.1.2"), // naziv pool-a
				new OID("1.3.6.1.4.1.9.9.48.1.1.1.5"), // used
				new OID("1.3.6.1.4.1.9.9.48.1.1.1.6") }; // free

		TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
		List<TableEvent> events = tableUtils.getTable(target, columns, null, null);

		for (TableEvent event : events) {
			if (event == null || event.getColumns() == null || event.isError())
				continue;
			VariableBinding[] vb = event.getColumns();
			String name = vb[0].getVariable().toString();
			long used = vb[1].getVariable().toLong();
			long free = vb[2].getVariable().toLong();
			result.add(new MemoryPoolInfo(name, used, free));
			// System.out.println(name + " " + "used =" + used + " free = " + free + " total
			// " + (used + free));
		}
		return result;
	}
}
