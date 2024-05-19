<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@axios";
import BeePlugin from '@beefree.io/sdk'
import { mergeTagsMap } from "@/@core/utils/index";

interface Props {
    nameValue: boolean,
}
interface Emit {
    (e: 'update:isDialogVisible', val: boolean): void
    (e: 'imageurl', value: string): void
}
const props = defineProps<Props>()
const color =  '#66C871'
const emit = defineEmits<Emit>()
const clientId = ref("");
const clientSecret = ref("");
const loading = ref(true);

 const bee = new BeePlugin();
 let beeConfig; 
    // GET USER Beefree clientId and clientSecret
    async function getBeeToken() {

      // clientId.value = "d4379066-c300-43de-a302-d964ec522fe2" //file manager key
      // clientSecret.value = "kLACUS2AmoeCv6mR8heJbmvybeBTNDt7puRACFJeGNsFEyVjm76A"
      // return;

      try {
        const response = await axios.get("/api/bee/beeKey",{
          params:{
            type:'filemanager'
          }
        });
        if (response && response.data) {
          const beeTokendata = response.data.split("&");
          clientId.value = beeTokendata[1]
            .match(/client_id=([^&]+)/)?.[1]
            .trim();
          clientSecret.value = beeTokendata[2]
            .match(/client_secret=([^&]+)/)?.[1]
            .trim();
        }
      } catch (error) {
        console.error("Error during initialization --> ", error);
      }
    }
    onMounted(async () => {
      await getBeeToken();
      const API_AUTH_URL = "https://bee-auth.getbee.io/apiauth";
      const BEEJS_URL = "https://app-rsrc.getbee.io/plugin/BeePlugin.js";
      const mytemplate = '{"page":{"template":{"name":"template-base","type":"basic","version":"0.0.1"},"body":{"content":{"style":{"color":"#000000","font-family":"Arial, \'Helvetica Neue\', Helvetica, sans-serif"},"computedStyle":{"messageBackgroundColor":"transparent","linkColor":"#0068A5","messageWidth":"700px"}},"container":{"style":{"background-color":"#FFFFFF"}},"type":"mailup-bee-page-proprerties","webFonts":[]},"title":"Empty Bee Template","description":"Template for BEE - Empty","rows":[{"content":{"style":{"color":"#000000","width":"500px","background-color":"transparent"}},"container":{"style":{"background-color":"transparent"}},"columns":[{"style":{"border-right":"0px dotted transparent","padding-left":"0px","padding-right":"0px","padding-top":"5px","border-bottom":"0px dotted transparent","background-color":"transparent","border-top":"0px dotted transparent","padding-bottom":"5px","border-left":"0px dotted transparent"},"grid-columns":12,"modules":[{"descriptor":{},"type":"mailup-bee-newsletter-modules-empty"}]}],"type":"one-column-empty"}]}}';

      const namevalue = props.nameValue;
      const containerId = "bee-plugin-container-e_receipts";

      const conf = { authUrl: API_AUTH_URL, beePluginUrl: BEEJS_URL };
      const requestFrom = "";

      if (
        (requestFrom == "" || requestFrom == "undefined") &&requestFrom != "e_receipt"
      ) {
        beeConfig = {
          uid: namevalue,
          container: containerId,
          autosave: false,
          onFilePickerInsert: function (data:Object) {
      // Handle the selected file data
            console.log(data['public-url']);
            emit('imageurl',data['public-url'])
          },
          
        };
      }
      /*verify bee cridentail */
      if (clientId.value && clientSecret.value) {
        bee
          .getToken(clientId.value, clientSecret.value, conf)
          .then(() => {
           
              bee
                .start(beeConfig, mytemplate, "", { shared: false })
                .then(
                  (loading.value = false)
                );
            
          })
          .catch((error) =>
            console.error("error during iniziatialization --> ", error)
          );
      }
    });

</script>

<template>

          <v-container>
    <!-- All Custom Butoon show-->
    <div v-if="!loading" id="bee-plugin-container-e_receipts" style="height: 100vh" class="mb-1">
    </div>
  </v-container>
  <!-- </VDialog> -->
</template>


<style scoped>
.loader-container {
  height: 100vh;
}
</style>
