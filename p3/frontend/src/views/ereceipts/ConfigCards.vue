<!-- ConfigCards.vue -->
<template>
  <div class="mb-3">
    <v-card>
      <div style="margin-left: 90%" v-if="selected && selected.__name && selected.__name == 'ReceiptSocialIcons' ||
        selected.__name == 'ReceiptVideoEmbed' ||
        selected.__name == 'ReceiptItemsList'">
        <VTooltip location=" bottom">
          <template #activator="{ props }">
            <v-icon icon="tabler-file-settings" variant="outlined"
              class="font-weight-regular mr-1 ml-0 mt-2  mb-0 cursor-pointer" v-bind="props" color="blue" size="35"
              @click="openDialog"></v-icon>
          </template>
          Configure
        </VTooltip>
      </div>
      <slot></slot>
    </v-card>
    <div class="modal-container" v-if="modalVisible" @click="closeDialogOutside">
      <div class="modal ma-2 pa-2">
        <v-card>
          <v-card-title class="text-center">Edit Configuration</v-card-title>
          <v-card-text>
            <template v-if="selected.__name === 'ReceiptVideoEmbed'">
              <v-text-field v-model="videoData.url" placeholder="Youtube Video Url"></v-text-field>
            </template>
            <template v-else-if="selected.__name === 'ReceiptSocialIcons'">
              <v-text-field v-model="socialData.facebook" placeholder="Facebook" class="my-2"></v-text-field>
              <v-text-field v-model="socialData.instagram" placeholder="Instagram" class="my-2"></v-text-field>
              <v-text-field v-model="socialData.twitter" placeholder="Twitter" class="my-2"></v-text-field>
              <v-text-field v-model="socialData.linkedin" placeholder="LinkedIn" class="my-2"></v-text-field>
              <v-text-field v-model="socialData.youtube" placeholder="Youtube" class="my-2"></v-text-field>
            </template>
            <template v-else-if="selected.__name === 'ReceiptItemsList'">
              <v-select v-model="selectedItem" :items="skuInventory" item-title="displayName" item-value="keyValue"
                return-object variant="outlined" label="Select Item Name"></v-select>

            </template>
          </v-card-text>
          <v-card-actions>
            <v-btn @click="saveAndClose">Save</v-btn>
            <v-btn @click="closeDialog">Close</v-btn>
          </v-card-actions>
        </v-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
interface videoData {
  url?: string;

}
interface socialData {

  facebook?: string;
  instagram?: string;
  twitter?: string;
  linkedin?: string;
  youtube?: string;
}
import { defineComponent, PropType } from 'vue';
import { useSharedStore } from '@/store';
const store = useSharedStore();

export default defineComponent({

  props: {
    configuration: Object as PropType<{ [key: string]: any }>,
    selected: Object as PropType<{ [key: string]: any }> // Assuming 'selected' is a string
  },
  data() {
    return {
      modalVisible: false,
      videoData: {
        url: ''
      },
      socialData: {
        facebook: '',
        instagram: '',
        twitter: '',
        linkedin: '',
        youtube: ''
      },
      itemsList: [],
      skuInventory: [
        { displayName: 'Item Category', keyValue: 'itemCategory' },
        { displayName: 'Department Code', keyValue: 'departmentCode' },
        { displayName: 'Description', keyValue: 'description' },
        { displayName: 'Class Code', keyValue: 'classCode' },

      ],
      selectedItem: { displayName: '', keyValue: '' }
    };
  },
  methods: {
    openDialog() {
      this.modalVisible = true;
    },
    saveAndClose() {
      if (this.selected.__name === 'ReceiptVideoEmbed') {
        let id = '';
        let embedlink;
        const urlRegex = /^https:\/\/(www\.)?youtube\.com\/.*$/;
        if (urlRegex.test(this.videoData.url)) {
          if (this.videoData.url.includes('?v=')) {
            id = this.videoData.url.split("?v=")[1];
            embedlink = `https://www.youtube.com/embed/${id}`;
          } else {
            embedlink = this.videoData.url;
          }

          this.videoData.url = embedlink;
          store.setVideoConfig(this.videoData);
          store.setReceiptConfigValidation('true')
        } else {
          store.setSnackbar({
            content: "Invalid Youtube Url",
            color: "error",
            isVisible: true,
            icon: 'tabler-exclamation-circle'
          });
          store.setReceiptConfigValidation('false')
        }
      }

      else if (this.selected.__name == 'ReceiptSocialIcons') {
        const urlRegex = /^https:\/\/[^\s/$.?#].[^\s]*$/i;
        let errorOccurred = false;
        for (const platform in this.socialData) {
          const url = this.socialData[platform as keyof socialData]; // Type assertion here
          if (url != '' && !urlRegex.test(url)) {
            store.setSnackbar({
              content: "Please Enter Valid Url",
              color: "error",
              isVisible: true,
              icon: 'tabler-exclamation-circle'
            });
            store.setReceiptConfigValidation('false')
            errorOccurred = true;
            break;
          }
        }
        if (!errorOccurred) {
          store.setReceiptConfigValidation('true')
          store.setSocialConfig(this.socialData);
        }
      } else if (this.selected.__name == 'ReceiptItemsList') {
        store.setReceiptItemNameConfig(this.selectedItem.keyValue)
      }
      this.modalVisible = false;
    },

    closeDialog() {
      this.modalVisible = false;
    },
    handleReceiptVideoEmbed(configuration: { [x: string]: any; url?: any; }) {
      if (configuration.url) {
        this.videoData.url = configuration.url;
        store.setVideoConfig(configuration);
      }
    },

    handleReceiptItemsList(configuration: { [x: string]: any; itemName?: any; }) {
      if (configuration.itemName != '') {
        this.selectedItem.displayName = configuration.itemName;
        store.setReceiptItemNameConfig(configuration.itemName)
      } else if (configuration.itemName == '') {
        this.selectedItem.displayName = 'Department Code';
        store.setReceiptItemNameConfig(configuration.itemName)

      }
    },

    handleReceiptSocialIcons(configuration: { facebook: string; instagram: string; twitter: string; linkedin: string; youtube: string; }) {
      this.socialData = configuration;
    },
    closeDialogOutside(event: { target: { closest: (arg0: string) => any; }; }) {
      if (!event.target.closest('.modal')) {
        this.closeDialog();
      }
    },
  },
  mounted() {
    const { configuration, selected } = this.$props;
    if (configuration) {
      switch (selected.__name) {
        case 'ReceiptVideoEmbed':
          this.handleReceiptVideoEmbed(configuration);
          break;
        case 'ReceiptItemsList':
          this.handleReceiptItemsList(configuration);
          break;
        case 'ReceiptSocialIcons':
          this.handleReceiptSocialIcons(configuration);
          break;
        default:
          console.warn('Unrecognized component type:', selected.__name);
      }
    }
  },
}
);
</script>

<style scoped>
.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw !important;
  height: 100vh !important;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000 !important;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.modal {
  background-color: #fff;
  border-radius: 5px;
  min-width: 580px;
  text-align: center;
}
</style>
