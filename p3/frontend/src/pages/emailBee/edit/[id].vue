<script>
import { ref } from "vue";
import Emailbee from "../components/Emailbee.vue"

export default {
  components: {
    Emailbee,
  },
  setup() {
        let templateJson = ref(null);
        const editTemplateName = ref("");
        let templateData = ''
        function setData() {
            templateData = JSON.parse(localStorage.getItem("mytemplate") || '{}');
            if (templateData && templateData.jsonContent) {
                templateJson.value = templateData.jsonContent;
                editTemplateName.value = templateData.templateName;
            }
        }
        setData();

        return {
            templateData,
            templateJson,
            editTemplateName
        };
    },

};
</script>
<template>
    <div>
        <section>
            <VCard class="pa-sm-8 pa-5">
                <VCardItem class="text-center">
                    <VCardTitle class="text-h4 mb-3 font-weight-medium">
                        Edit Email Template
                    </VCardTitle>
                </VCardItem>
                <VRow>
                    <VCol cols="12" lg="12" md="12" sm="6">
                        <Emailbee templateType="edit_template" beeConfigId="emailbee-edit-template" :templateJson="templateJson" :editTemplateName="editTemplateName" :editTemplateData="templateData"/>
                    </VCol>
                </VRow>
            </VCard>
        </section>
    </div>
</template>

