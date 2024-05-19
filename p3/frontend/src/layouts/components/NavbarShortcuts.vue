<script setup lang="ts">
const shortcuts = [
  {
    icon: 'tabler-users',
    title: 'Customers',
    subtitle: 'Lookup Customer',
    // to: { name: 'apps-calendar' },
  },
  {
    icon: 'tabler-send',
    title: 'Campaigns',
    subtitle: 'Send Campaigns',
    // to: { name: 'apps-invoice-list' },
  },
  {
    icon: 'tabler-ticket',
    title: 'Coupons',
    subtitle: 'Create and Issue',
    // to: { name: 'apps-user-list' },
  },
  {
    icon: 'tabler-discount-2',
    title: 'Discounts',
    subtitle: 'Create Promotions',
    // to: { name: 'dashboards-analytics' },
  },
  {
    icon: 'tabler-settings',
    title: 'Settings',
    subtitle: 'Account Settings',
    // to: { name: 'pages-account-settings-tab', params: { tab: 'account' } },
  },
  {
    icon: 'tabler-help',
    title: 'Help Center',
    subtitle: 'FAQs & Articles',
    // to: { name: 'pages-help-center' },
  },
]
</script>

<template>
  <Shortcuts :shortcuts="shortcuts" />
</template>
