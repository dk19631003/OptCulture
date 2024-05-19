<script lang="ts" setup>
import VueApexCharts from 'vue3-apexcharts'
import { useTheme } from 'vuetify'

const vuetifyTheme = useTheme()

const chartConfig = {
  chart: {
    parentHeightOffset: 0,
    toolbar: { show: false },
  },
  dataLabels: { enabled: false },
  stroke: {
    // colors: [themeColors.colors.surface],
  },
  legend: {
    position: 'bottom',
    fontSize: '13px',
    labels: {
      // colors: themeSecondaryTextColor,
    },
    markers: {
      offsetY: 0,
      offsetX: -3,
    },
    itemMargin: {
      vertical: 3,
      horizontal: 10,
    },
  },
  plotOptions: {
    heatmap: {
      enableShades: false,
      colorScale: {
        ranges: [
          { to: 1, from: 0, name: '1', color: '#b9b3f8' },
          { to: 2, from: 1, name: '2', color: '#aba4f6' },
          { to: 3, from: 2, name: '3', color: '#9d95f5' },
          { to: 4, from: 3, name: '4', color: '#8f85f3' },
          { to: 5, from: 4, name: '5', color: '#8176f2' },
        ],
      },
    },
  },
  grid: {
    padding: { top: -20 },
  },
  yaxis: {
    labels: {
      show: false,
      style: {
        // colors: themeDisabledTextColor,
        fontSize: '0.8125rem',
      },
    },
  },
  xaxis: {
    labels: { show: false },
    axisTicks: { show: false },
    axisBorder: { show: false },
  },
}

interface YRange {
  min: number
  max: number
}

const generateDataHeat = (count: number, yrange: YRange) => {
  let i = 0
  const series = []
  while (i < count) {
    const x = `w${(i + 1).toString()}`
    const y = Math.floor(Math.random() * (yrange.max - yrange.min + 1)) + yrange.min

    series.push({
      x,
      y,
    })
    i += 1
  }

  return series
}

const series = [
  {
    name: 'SUN',
    data: generateDataHeat(5, { min: 1, max: 5 }),
  },
  {
    name: 'MON',
    data: generateDataHeat(5, { min: 1, max: 5 }),
  },
  {
    name: 'TUE',
    data: generateDataHeat(5, { min: 1, max: 5 }),
  },
  {
    name: 'WED',
    data: generateDataHeat(5, { min: 1, max: 5 }),
  },
  {
    name: 'THU',
    data: generateDataHeat(5, { min: 1, max: 5 }),
  },
]
</script>

<template>
  <VueApexCharts
    type="heatmap"
    height="280"
    :options="chartConfig"
    :series="series"
  />
</template>
