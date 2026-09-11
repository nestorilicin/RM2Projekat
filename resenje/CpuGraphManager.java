package resenje;

import java.util.*;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import java.awt.*;

public class CpuGraphManager {
	private Map<String, TimeSeries[]> cpuSeriesMap = new LinkedHashMap<>();
	private Map<String, ChartPanel> chartPanelMap = new LinkedHashMap<>();
	private List<String> routers;

	public CpuGraphManager(List<String> routers) {
		this.routers = routers;
		for (String router : routers) {
			TimeSeries cpu5s = new TimeSeries("CPU 5s");
			TimeSeries cpu1min = new TimeSeries("CPU 1min");
			TimeSeries cpu5min = new TimeSeries("CPU 5min");

			cpuSeriesMap.put(router, new TimeSeries[] { cpu5s, cpu1min, cpu5min });

			TimeSeriesCollection dataset = new TimeSeriesCollection();
			dataset.addSeries(cpu5s);
			dataset.addSeries(cpu1min);
			dataset.addSeries(cpu5min);

			JFreeChart chart = ChartFactory.createTimeSeriesChart("CPU opterećenje - " + router, "Vreme", "%", dataset,
					true, true, false);

			styleChart(chart);

			ChartPanel panel = new ChartPanel(chart);
			chartPanelMap.put(router, panel);
		}
	}

	public JPanel getPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		for (String router : routers) {
			panel.add(chartPanelMap.get(router));
		}
		return panel;
	}

	public void addCpuData(String router, long[] values) {
		TimeSeries[] series = cpuSeriesMap.get(router);
		if (series == null)
			return;

		Millisecond now = new Millisecond();
		for (int i = 0; i < values.length && i < series.length; i++) {
			series[i].addOrUpdate(now, values[i]);
		}
	}

	private void styleChart(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.BLACK);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		chart.setBackgroundPaint(Color.LIGHT_GRAY);
		chart.getTitle().setPaint(Color.BLACK);
		chart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
		chart.getLegend().setItemPaint(Color.WHITE);

		XYItemRenderer renderer = plot.getRenderer();
		Color[] vibrantColors = new Color[] { Color.CYAN, Color.YELLOW, Color.MAGENTA };
		for (int i = 0; i < vibrantColors.length; i++) {
			renderer.setSeriesPaint(i, vibrantColors[i]);
		}
	}
}
