export default [
  { heading: 'Communication', },
  {
    title: 'Automation',
    icon: { icon: 'tabler-settings-automation' },
    children: [
      { title: 'Event Triggers', to: 'pages-misc-coming-soon' },
      { title: 'Loyalty Messages', to: 'pages-misc-coming-soon' },
    ],
    action: 'View',
    subject: 'OCAdmin'
  },
  //  {
  //     title: 'Email Demo',
  //     icon: { icon: 'tabler-mail' },
  //     to: 'emaildemo',
  //   },
  {
    title: 'Campaigns',
    icon: { icon: 'tabler-send' },
    to: 'campaigns-list',
    // children: [
    //   { title: 'Campaign List', to: 'campaigns-list' },
    //    { title: 'Email', to: 'campaigns-email' },
    //    { title: 'SMS', to: 'campaigns-sms' },
    //   { title: 'WhatsApp', to: 'campaigns-whatsapp' },
    //   {
    //     title: 'Bee Templates ',
    //     to: 'emailBee',
    //   }
    // ],
  },
  {
    title: 'Email Templates ',
    to: 'emailBee',
    icon: { icon: 'tabler-mail-code' }
  },
  {
    title:'Email Settings',
    to:'campaigns-email-settings',
    icon:{icon:'tabler-mail-cog'}
  },
  {
    title: 'Channel Settings',
    to: 'channels',
    icon: { icon: 'tabler-settings-filled' },
    action: 'View',
    subject: 'OCAdmin'
  },

  {
    title: 'E-Receipts',
    to: 'ereceipts',
    icon: { icon: 'tabler-receipt' },
    action: 'View',
    subject: 'OCAdmin'
  },
  {
    title: 'Feedback',
    to: 'pages-misc-coming-soon',
    icon: { icon: 'tabler-checklist' },
    action: 'View',
    subject: 'OCAdmin'
  },
]

