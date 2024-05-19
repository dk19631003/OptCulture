<script setup lang="ts">
import VueApexCharts from 'vue3-apexcharts'
import { useTheme } from 'vuetify'
import { hexToRgb } from '@layouts/utils'

const vuetifyTheme = useTheme()

const series = [
  {
    data: [40, 65, 50, 45, 90, 55, 70],
  },
]

const chartOptions = computed(() => {
  const currentTheme = vuetifyTheme.current.value.colors
  const variableTheme = vuetifyTheme.current.value.variables

  return {
    chart: {
      parentHeightOffset: 0,
      type: 'bar',
      toolbar: {
        show: false,
      },
    },
    plotOptions: {
      bar: {
        barHeight: '60%',
        columnWidth: '55%',
        startingShape: 'rounded',
        endingShape: 'rounded',
        borderRadius: 4,
        distributed: true,
      },
    },
    grid: {
      show: false,
      padding: {
        top: -30,
        bottom: 0,
        left: -10,
        right: -10,
      },
    },
    colors: [
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
      `rgba(${hexToRgb(currentTheme.primary)}, 1)`,
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
      `rgba(${hexToRgb(currentTheme.primary)},${variableTheme['pressed-opacity']})`,
    ],
    dataLabels: {
      enabled: false,
    },
    legend: {
      show: false,
    },
    xaxis: {
      categories: ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'],
      axisBorder: {
        show: false,
      },
      axisTicks: {
        show: false,
      },
      labels: {
        style: {
          colors: `rgba(${hexToRgb(currentTheme['on-surface'])},${variableTheme['disabled-opacity']})`,
          fontSize: '13px',
          fontFamily: 'Public Sans',
        },
      },
    },
    yaxis: {
      labels: {
        show: false,
      },
    },
    tooltip: {
      enabled: false,
    },
    responsive: [
      {
        breakpoint: 1025,
        options: {
          chart: {
            height: 199,
          },
        },
      },
    ],
  }
})

const earningsReports = [
  {
    color: 'primary',
    icon: 'tabler-currency-dollar',
    title: 'Avg. Order Value',
    amount: '$545.69',
    progress: '55',
  },
  {
    color: 'primary',
    icon: 'tabler-calendar-event',
    title: 'Frequency',
    amount: '2.3 orders',
    progress: '25',
  },
  {
    color: 'primary',
    icon: 'tabler-rotate-clockwise-2',
    title: 'Cycle',
    amount: '23 days',
    progress: '65',
  },
]

const moreList = [
  { title: 'View More', value: 'View More' },
  { title: 'Delete', value: 'Delete' },
]
</script>

<template>
  <VCard>
    <VCardItem class="pb-sm-0">
      <VCardTitle>Life Time Value</VCardTitle>
      <VCardSubtitle>Three year value</VCardSubtitle>

      <template #append>
        <div class="mt-n4 me-n2">
          <MoreBtn :menu-list="moreList" />
        </div>
      </template>
    </VCardItem>

    <VCardText class="mt-sm-n6">
      <VRow>
        <VCol
          cols="12"
          sm="5"
          class="d-flex flex-column align-self-end"
        >
          <div class="d-flex align-center gap-2 mb-2 pb-1 flex-wrap">
            <h4 class="text-4xl font-weight-medium">
              $468
            </h4>
            <VChip
              label
              color="success"
            >
              +4.2%
            </VChip>
          </div>

          <p class="text-sm">
            compared to last month
          </p>
        </VCol>

        <VCol
          cols="12"
          sm="7"
          class="pt-0"
        >
          <VueApexCharts
            :options="chartOptions"
            :series="series"
            height="190"
          />
        </VCol>
      </VRow>

      <div class="border rounded mt-3 px-5 py-4">
        <VRow>
          <VCol
            v-for="report in earningsReports"
            :key="report.title"
            cols="12"
            sm="4"
          >
            <div class="d-flex align-center">
              <VAvatar
                rounded
                size="26"
                :color="report.color"
                variant="tonal"
                class="me-2"
              >
                <VIcon
                  size="18"
                  :icon="report.icon"
                />
              </VAvatar>

              <h6 class="text-base font-weight-medium">
                {{ report.title }}
              </h6>
            </div>
            <h4 class="text-h4 my-3">
              {{ report.amount }}
            </h4>
          </VCol>
        </VRow>
      </div>
    </VCardText>
  </VCard>
</template>
