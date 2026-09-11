package resenje;

import java.util.*;
import java.util.List;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import java.awt.*;

public class MemGraphManager {
	private Map<String, Map<String, TimeSeries>> memSeriesMap = new LinkedHashMap<>();
	private Map<String, ChartPanel> chartPanelMap = new LinkedHashMap<>();
	private List<String> routers;

	public MemGraphManager(List<String> routers) {
		this.routers = routers;
		for (String router : routers) {
			memSeriesMap.put(router, new HashMap<String, TimeSeries>());
			TimeSeriesCollection dataset = new TimeSeriesCollection();

			JFreeChart chart = ChartFactory.createTimeSeriesChart("Zauzeta i slobodna memorija po poolovima " + router,
					"Memorija", "MB", dataset, true, true, false);

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

	public void addMemoryData(String router, List<MemoryPoolInfo> pools) {
		Millisecond now = new Millisecond();
		Map<String, TimeSeries> seriesMap = memSeriesMap.get(router);

		ChartPanel panel = chartPanelMap.get(router);
		JFreeChart chart = panel.getChart();
		XYPlot plot = chart.getXYPlot();
		TimeSeriesCollection dataset = (TimeSeriesCollection) plot.getDataset();

		for (MemoryPoolInfo pool : pools) {
			/*
			 * String name = pool.getName(); double percent = pool.getUsagePercentage();
			 * 
			 * TimeSeries ts = seriesMap.get(name); if (ts == null) { ts = new
			 * TimeSeries(name); seriesMap.put(name, ts); dataset.addSeries(ts); }
			 * 
			 * ts.addOrUpdate(now, percent);
			 */

			String baseName = pool.getName();

			long used = pool.getUsed() / (1024 * 1024);
			long total = pool.getTotal() / (1024 * 1024);
			long free = total - used;

			String usedName = baseName + " Used";
			TimeSeries usedSeries = seriesMap.get(usedName);
			if (usedSeries == null) {
				usedSeries = new TimeSeries(usedName);
				seriesMap.put(usedName, usedSeries);
				dataset.addSeries(usedSeries);
			}
			usedSeries.addOrUpdate(now, used);

			String freeName = baseName + " Free";
			TimeSeries freeSeries = seriesMap.get(freeName);
			if (freeSeries == null) {
				freeSeries = new TimeSeries(freeName);
				seriesMap.put(freeName, freeSeries);
				dataset.addSeries(freeSeries);
			}
			freeSeries.addOrUpdate(now, free);
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
		Color[] vibrantColors = new Color[] { Color.GREEN, Color.CYAN, Color.MAGENTA };
		for (int i = 0; i < vibrantColors.length; i++) {
			renderer.setSeriesPaint(i, vibrantColors[i % vibrantColors.length]);
		}
	}
}
