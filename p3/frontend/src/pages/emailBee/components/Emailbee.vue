<script>
import { mergeTagsMap } from "@/@core/utils/index";
import axios from "@axios";
import router from '@/router';
//import BeePlugin from "@mailupinc/bee-plugin";
import BeePlugin from '@beefree.io/sdk'
import { getCurrentInstance, ref } from "vue";

const clientId = ref("");
const clientSecret = ref("");
const loading = ref(true);
const showButtonsAfterLoad = ref(false);
const disCodeload = ref(true);
const discountCodes = ref([]);
const mergeTagsList = ref([]);
const modalSaveRowVisible = ref(false);
const modalSaveTempVisible = ref(false);
const rowName = ref("");
const templateName = ref("");
const showError = ref(false);
const errorText = ref("");
const showSuccess = ref(false);
const sucessText = ref('');
const externalUrl = ref("");
const mytemplate = ref('')
const updateTemplate = ref(false)
const editTemplateName = ref("");
const newTemplateName = ref("");
const barCodes = ref([]);
const lastSaveDate = ref('')
const redirctRoute = ref(true)
const userData = JSON.parse(localStorage.getItem('userData') || 'null')



/*Load Merge tags*/
for (const [key, value] of mergeTagsMap.entries()) {
  const obj = { title: "", value: "" };
  obj.name = key;
  obj.value = value.tag;
  mergeTagsList.value.push(obj);
}

export default {

  props: {
    templateType: String,
    templateJson: String,
    isTemplateChange: Boolean,
    isAddTemplateVisible: Boolean,
    communicationId: Number,
    saveContentPopup: Boolean,
    editTemplateName: String,
    beeConfigId: String,
    editTemplateData: String
  },
  computed: {
    dialogVisible: {
      get() {
        return this.saveContentPopup;
      },
      set(value) {
        if (!value) {
          this.$emit('close-dialog');
        }
      }
    }
  },
  data() {
    return {
      category: "My Custom Row", // Set the default selected item
      timeout: 2000, // Hide Error after 2 second
    };
  },
  setup(props) {
    const bee = new BeePlugin();
    const instance = getCurrentInstance();
    const emit = instance.emit;
    let beeConfig;

    const encodeBeeEditorBodyJSONText = (jsonFileText) => {
      let finalJsonFileText = "";
      let charUnicode;
      for (const codePoint of jsonFileText) {
        charUnicode = codePoint.codePointAt(0).toString(16);
        if (charUnicode.length == 5 || charUnicode.length == 4) {
          charUnicode = "&#x" + charUnicode + ";";
          finalJsonFileText += charUnicode;
        } else {
          finalJsonFileText += codePoint;
        }
      }
      //console.log('finalJsonFileText:', finalJsonFileText);
      return finalJsonFileText;
    };

    const validateJson = (beeJsonData) => {
      const DEFAULT_JSON_CHECK_TEXTBOX = "mailup-bee-newsletter-modules-text";
      const DEFAULT_JSON_CHECK_IMAGE = "mailup-bee-newsletter-modules-image";
      const DEFAULT_JSON_CHECK_VIDEO = "mailup-bee-newsletter-modules-video";
      const DEFAULT_JSON_CHECK_BUTTON = "mailup-bee-newsletter-modules-button";
      const DEFAULT_JSON_CHECK_DIVIDER = "mailup-bee-newsletter-modules-divider";
      const DEFAULT_JSON_CHECK_MERGE_CONTENT = "mailup-bee-newsletter-modules-merge-content";
      const DEFAULT_JSON_CHECK_HTML = "mailup-bee-newsletter-modules-html";
      const DEFAULT_JSON_CHECK_SOCIAL = "mailup-bee-newsletter-modules-social";
      const DEFAULT_JSON_CHECK_TITLE = "mailup-bee-newsletter-modules-heading";
      const DEFAULT_JSON_CHECK_PARAGRAPH = "mailup-bee-newsletter-modules-paragraph";
      const DEFAULT_JSON_CHECK_LIST = "mailup-bee-newsletter-modules-list";

      if (beeJsonData != null &&
        (beeJsonData.includes(DEFAULT_JSON_CHECK_TEXTBOX) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_IMAGE) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_MERGE_CONTENT) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_HTML) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_BUTTON) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_SOCIAL) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_DIVIDER) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_VIDEO) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_TITLE) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_PARAGRAPH) ||
          beeJsonData.includes(DEFAULT_JSON_CHECK_LIST)
        )) {
        return true
      } else {
        return false
      }
    };
    // Method to handle the 'Preiview' event from BEE Plugin
    const showPreview = () => {
      bee.togglePreview();
    };

    //Update template josn if Edit template
    function setTemplatetoLocStrge(item) {
      localStorage.removeItem('mytemplate');
      localStorage.setItem('mytemplate', JSON.stringify(item));
    }

    // Method to handle the 'save' event from BEE Plugin
    const savejsonhtmlcontent = (type) => {
      if (props.templateType == 'selctedChampain') {
        updateTemplate.value = false
        if (type == 'popup') {
          setTimeout(() => {
            emit('nextStep')
          }, 1000);
        }
      } else if (props.templateType == 'edit_template') {
        updateTemplate.value = true
        templateName.value = props.editTemplateName
        redirctRoute.value = false
      } else {
        templateName.value = editTemplateName.value
        updateTemplate.value = true
      }
      bee.save();
    };

    const closeSaveClose = () => {
      emit('close-dialog')
    }

    //Save Template Model open
    const handleSaveTemplate = () => {
      newTemplateName.value = ''
      modalSaveTempVisible.value = true;
    };

    // Method to handle the 'save template' event from BEE Plugin ST
    const saveTemplate = () => {
      const tempname = newTemplateName.value;
      if (tempname) {
        // bee.saveAsTemplate();
        updateTemplate.value = false
        templateName.value = newTemplateName.value
        bee.save();
      } else {
        showError.value = true;
        errorText.value = "Please provide a Template name.";
      }
    };

    //Save Template Model Close
    const cancelSaveTemplate = () => {
      modalSaveTempVisible.value = false;
    };

    // Method to handle the 'send template' event from BEE Plugin
    const sendTemplate = () => {
      bee.send();
    };

    //Save row Model Close
    const cancelSaveRow = () => {
      modalSaveRowVisible.value = false;
    };

    //Created Template Send Logic
    const send = (filename, htmlFile) => {
      // Your send logic here
      // ...
    };


    // Loads all Discount Codes
    async function loadAllDiscountCodes() {
      let currentPage = 0;
      let allDiscountCodes = [];
      try {
        let response;
        do {
          response = await axios.get("/api/coupons/inventory", {
            params: {
              pageNumber: currentPage,
              pageSize: 100
            },
          });

          // Check if response is truthy before accessing its properties
          if (response && response.data && response.data.content) {
            // Append the content of the current page to the result array
            allDiscountCodes = allDiscountCodes.concat(response.data.content);
          }
          // Move to the next page
          currentPage++;
        } while (response && currentPage < response.data.totalPages);

        allDiscountCodes.forEach((item) => {
          const obj = {
            type: "Discount Codes",
            label: item.couponName,
            link: item.couponCode,
          };
          discountCodes.value.push(obj);
        });
        disCodeload.value = true;
      } catch (error) {
        console.error("Error during initialization --> ", error);
      }
    }

    //Load External URL Show in ROWS
    async function loadExternalUrl() {
      try {
        const response = await axios.get("/api/bee/externalUrl?type=campaign");
        externalUrl.value = response.data;
      } catch (error) {
        console.error("Error during initialization --> ", error);
      }
    }

    async function loadBarcodes() {
      try {
        const response = await axios.get("/api/bee/dynamicBarCodes");
        if (response.data) {
          const result = response.data.map(item => {
            const name = item.match(/name: '([^']+)'/)[1];
            const value = item.match(/value: '([^']+)'/)[1];
            return { name, value };
          });
          barCodes.value = result
        }
      } catch (error) {
        console.error("Error during initialization --> ", error);
      }
    }

    // GET USER Beefree clientId and clientSecret
    async function getBeeToken() {
      try {
        const response = await axios.get("/api/bee/beeKey?type=editor");
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

    //Save Row name validate and
    const handleSaveRow = (resolve, reject, row) => {
      modalSaveRowVisible.value = true;
      // Return a promise that resolves when the model is closed
      const handleSaveClick = async () => {
        try {
          const name = rowName.value;
          if (name) {
            // Resolve with the saved data
            modalSaveRowVisible.value = false;

            const metadata = {
              name: name,
              category: "My Custom Row",
            };
            resolve(metadata);
          } else {
            showError.value = true;
            errorText.value = "Please provide a Row name.";
          }
        } catch (error) {
          showError.value = true;
          reject(error);
        }
      };
      modalSaveRowVisible.value = handleSaveClick;
    };



    onBeforeMount(async () => {
      loading.value = true
      lastSaveDate.value = ''
      showSuccess.value = false
      showError.value = false

      await getBeeToken();
      await loadAllDiscountCodes();
      await loadExternalUrl();
      await loadBarcodes();


      const API_AUTH_URL = "https://bee-auth.getbee.io/apiauth";
      const BEEJS_URL = "https://app-rsrc.getbee.io/plugin/BeePlugin.js";
      if ((props.templateType == 'selctedChampain' || props.templateType == 'edit_template') && props.templateJson) {
        mytemplate.value = props.templateJson
      } else {
        mytemplate.value = '{"page":{"template":{"name":"template-base","type":"basic","version":"0.0.1"},"body":{"content":{"style":{"color":"#000000","font-family":"Arial, \'Helvetica Neue\', Helvetica, sans-serif"},"computedStyle":{"messageBackgroundColor":"transparent","linkColor":"#0068A5","messageWidth":"700px"}},"container":{"style":{"background-color":"#FFFFFF"}},"type":"mailup-bee-page-proprerties","webFonts":[]},"title":"Empty Bee Template","description":"Template for BEE - Empty","rows":[{"content":{"style":{"color":"#000000","width":"500px","background-color":"transparent"}},"container":{"style":{"background-color":"transparent"}},"columns":[{"style":{"border-right":"0px dotted transparent","padding-left":"0px","padding-right":"0px","padding-top":"5px","border-bottom":"0px dotted transparent","background-color":"transparent","border-top":"0px dotted transparent","padding-bottom":"5px","border-left":"0px dotted transparent"},"grid-columns":12,"modules":[{"descriptor":{},"type":"mailup-bee-newsletter-modules-empty"}]}],"type":"one-column-empty"}]}}';
      }

      const namevalue = userData.userName;
      const containerId = props.beeConfigId;
      const conf = { authUrl: API_AUTH_URL, beePluginUrl: BEEJS_URL };

      const mergeTags97 = mergeTagsList.value;
      const barCodes79 = barCodes.value;
      const requestFrom = "";

      if (
        (requestFrom == "" || requestFrom == "undefined") &&
        requestFrom != "e_receipt"
      ) {
        beeConfig = {
          uid: namevalue,
          container: containerId,
          autosave: false,
          trackChanges: true,
          language: "en-US",
          specialLinks: [],
          mergeTags: mergeTags97,
          mergeContents: barCodes79,
          sidebarPosition: "right",
          loadingSpinnerDisableOnDialog: true,
          contentDialog: {
            saveRow: {
              handler: handleSaveRow,
            },
          },
          rowsConfiguration: {
            emptyRows: true,
            defaultRows: true,
            externalContentURLs: externalUrl.value,
          },
          onChange: function (jsonFile, response) {
            emit('isValidTemplate', validateJson(jsonFile));
            emit('updateJsonContent', jsonFile);
            // jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
          },
          onSave: function (jsonFile, htmlFile) {
            const jsonData = {
              json: jsonFile,
              html: htmlFile,
              templateName: templateName.value
            };
            modalSaveTempVisible.value = false;
            setTimeout(() => {
              emit('update:isAddTemplateVisible', false);
            }, 1500);

            if (validateJson(jsonFile)) {
              if (props.templateType == 'selctedChampain') {
                if (props.communicationId) {
                  const postData = {
                    commId: props.communicationId,
                    json: jsonFile,
                    html: htmlFile
                  }
                  axios
                    .post('/api/campaigns/saveBeeCommunicationTemplate', postData).
                    then(() => {
                      showSuccess.value = true
                      sucessText.value = "Template  Save Successfully"
                    })
                    .catch((error) => {
                      console.error("Error:", error.message);
                    });
                }
                emit('updateJsonContent', jsonFile);
                emit('updateHtmlContent', htmlFile);
              } else {
                if (jsonFile.length < 1600) {
                  showError.value = true;
                  errorText.value = "Template cannot be Empty!";
                  modalSaveTempVisible.value = false;
                }
                else {
                  axios
                    .post('/api/bee/saveTemplate', jsonData)
                    .then((response) => {
                      modalSaveTempVisible.value = false;
                      showSuccess.value = true
                      sucessText.value = "Template Save Successfully"
                      setTimeout(() => {
                        emit('update:isAddTemplateVisible', false);
                      }, 1500);
                      if (props.templateType == 'edit_template') {
                        props.editTemplateData.jsonContent = jsonFile
                        setTemplatetoLocStrge(props.editTemplateData)
                        let date = new Date();
                        let options = {
                          year: 'numeric',
                          month: '2-digit',
                          day: '2-digit',
                          hour: '2-digit',
                          minute: '2-digit',
                          second: '2-digit'
                        };
                        lastSaveDate.value = new Intl.DateTimeFormat('en-US', options).format(date);
                        if (redirctRoute.value) {
                          router.push('/emailbee')
                        }
                      } else if (props.templateType == 'add_new_template') {
                        showSuccess.value = true
                        sucessText.value = "Template Save Successfully"
                        setTimeout(() => {
                          emit('toggleTemplateLoad')
                        }, 1000);
                      }
                    })
                    .catch((error) => {
                      console.error("Error:", error.message);
                      modalSaveTempVisible.value = false;
                    });
                }
              }
            } else {
              showError.value = true;
              errorText.value = "Template cannot be Empty!";
            }

          },
          onSaveAsTemplate: function (jsonFile) {
            jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
          },
          onSend: function (htmlFile) {
            send("newsletters.html", htmlFile);
          },
          onError: function (errorMessage) {
            console.log("onError ", errorMessage);
          },
          onSaveRow: function (rowJSON, rowHTML, pageJSON) {
            const jsonData = {
              json: rowJSON,
              html: rowHTML
            };
            axios
              .post('/api/bee/saveCustomRow', jsonData)
              .then((response) => {

                console.log("Response:", response.data);
              })
              .catch((error) => {
                console.error("Error:", error.message);
              });
          },
        };
      }
      /*verify bee cridentail */
      if (clientId.value && clientSecret.value) {
        bee
          .getToken(clientId.value, clientSecret.value, conf)
          .then(() => {
            if (disCodeload.value) {
              emit('isValidTemplate', validateJson(mytemplate.value));
              beeConfig["specialLinks"] = discountCodes.value;
              bee
                .start(beeConfig, mytemplate.value, "", { shared: false })
                .then(
                  () => (showButtonsAfterLoad.value = true),
                  (loading.value = false)
                  // (emit('update:isTemplateChange', true))
                );
            }
          })
          .then(() => {
            watch(() => props.templateJson, (newValue, oldValue) => {
              emit('update:isTemplateChange', false)
              // console.log(`The state changed from ${oldValue} to ${newValue}`)
              if (!loading.value && props.isTemplateChange) {
                emit('isValidTemplate', validateJson(newValue));
                bee.load(newValue);
              }
            });
          })
          .catch((error) =>
            console.error("error during iniziatialization --> ", error)
          );
      }


    });



    return {
      bee,
      loading,
      showButtonsAfterLoad,
      modalSaveRowVisible,
      modalSaveTempVisible,
      rowName,
      templateName,
      showError,
      errorText,
      showSuccess,
      sucessText,
      showPreview,
      saveTemplate,
      sendTemplate,
      savejsonhtmlcontent,
      cancelSaveRow,
      cancelSaveTemplate,
      handleSaveTemplate,
      handleSaveRow,
      closeSaveClose,
      newTemplateName,
      lastSaveDate
    };
  },
};
</script>

<template>
  <v-container>
    <!-- All Custom Butoon show-->
    <div v-if="!loading" :id="beeConfigId" style="height: 100vh" class="mb-1">
      <v-sheet v-if="showButtonsAfterLoad" class="mb-2">
        <v-row class="d-flex">
          <v-col sm="6" md="6">
            <v-sheet class="ma-2 me-auto">
              <v-row>
                <v-col md="4" sm="12"
                  v-if="templateType == 'add_new_template' || templateType == 'edit_template' || templateType == 'add_campain_template'">
                  <v-btn prepend-icon="tabler-template" color="blue-darken-3" @click="handleSaveTemplate">
                    <template v-slot:prepend>
                      <v-icon color="white"></v-icon>
                    </template>
                    Save as template
                  </v-btn>
                </v-col>
                <v-col md="4" sm="12" v-if="templateType == 'selctedChampain' || templateType == 'edit_template'">
                  <v-btn ref="saveButton" prepend-icon="tabler-circle-check-filled" color="blue-darken-3"
                    @click="savejsonhtmlcontent">
                    <template v-slot:prepend>
                      <v-icon color="white"></v-icon>
                    </template>
                    Save
                  </v-btn>
                </v-col>
              </v-row>
            </v-sheet>
          </v-col>
          <v-col sm="6" md="6">
            <v-sheet class="ma-2">
              <v-row class="d-flex justify-end">
                <v-col class="d-flex justify-end" md="9" sm="12">
                  <v-btn prepend-icon="tabler-eye-filled" color="blue-darken-3" class="ml-2" @click="showPreview">
                    <template v-slot:prepend>
                      <v-icon color="white"></v-icon>
                    </template>
                    Preview
                  </v-btn>

                </v-col>
                <v-col class="d-flex justify-end" md="3" sm="12" v-if="templateType == 'selctedChampain'">
                  <v-btn prepend-icon="tabler-send" color="blue-darken-3" class="ml-2" @click="sendTemplate">
                    <template v-slot:prepend>
                      <v-icon color="white"></v-icon>
                    </template>
                    Send
                  </v-btn>
                </v-col>
              </v-row>
            </v-sheet>
          </v-col>
        </v-row>
        <v-row class="mb-5" v-if="lastSaveDate">
          <div class="text-subtitle-2">
            Last Save Date {{ lastSaveDate }}
          </div>
        </v-row>
      </v-sheet>

    </div>

    <!-- Dilog open for the  row save-->
    <v-dialog v-model="modalSaveRowVisible" max-width="500px">
      <v-card>
        <v-card-title class="headline">Save Row</v-card-title>
        <v-card-text>
          <AppSelect class="text-ellipsis" :items="['My Custom Row']" v-model="category" />
        </v-card-text>
        <v-card-text>
          <AppTextField placeholder="Row name" type="text" v-model="rowName" />
        </v-card-text>
        <v-card-actions>
          <v-btn @click="modalSaveRowVisible" color="primary">Save</v-btn>
          <v-btn @click="cancelSaveRow" color="error">Cancel</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dilog open for the  SAVE TEMPLATE-->
    <v-dialog v-model="modalSaveTempVisible" max-width="500px">
      <v-card>
        <v-card-title class="headline">Save Template</v-card-title>
        <v-card-text>
          <AppTextField placeholder="Template name" type="text" v-model="newTemplateName" />
        </v-card-text>
        <v-card-actions>
          <v-btn @click="saveTemplate" color="primary">Save</v-btn>
          <v-btn @click="cancelSaveTemplate" color="error">Cancel</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Loader -->
    <v-row v-if="loading" align="center" justify="center" class="loader-container">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
      <p>Loading...</p>
    </v-row>
    <!-- Show  Error Mesage  -->
    <v-snackbar v-model="showError" :timeout="timeout" color="red-darken-1" location="top center">
      {{ errorText }}

      <template v-slot:actions>
        <v-btn color="black" variant="text" @click="showError = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>

    <!-- Show  Success Mesage  -->
    <v-snackbar v-model="showSuccess" :timeout="timeout" color="teal-accent-4" location="top center">
      {{ sucessText }}

      <template v-slot:actions>
        <v-btn color="black" variant="text" @click="showSuccess = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>

    <!-- Next Button popup -->
    <v-dialog v-model="dialogVisible" max-width="500px">
      <v-card>
        <v-card-title>Confirmation</v-card-title>
        <v-card-text>
          Would you like to save Current Email Content?
        </v-card-text>
        <v-card-actions>
          <v-btn @click="savejsonhtmlcontent('popup')">Yes</v-btn>
          <v-btn @click="closeSaveClose">No</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<style scoped>
.loader-container {
  height: 100vh;
}
</style>
