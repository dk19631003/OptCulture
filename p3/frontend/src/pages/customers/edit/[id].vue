<script setup lang="ts">
import AppSelect from '@/@core/components/app-form-elements/AppSelect.vue'
import axios from '@axios'
import { ref } from 'vue'
import { emailValidator, lengthValidator, requiredValidator } from '@validators'
import { phoneValidator } from '@/@core/utils/localeFormatter'
import { validateZip } from '@/@core/utils/localeFormatter'
import { isEmpty } from '@/@core/utils'
import { useRouter } from "vue-router";
const router = useRouter()
const isEmailValid = ref(true)
const isMobileValid = ref(true)
const isZipValid = ref(true)

let contact = ref();
const userData = JSON.parse(localStorage.getItem('userData'))
const posMappingList = userData.posMappingList;
// console.log(posMappingList)
const country = ref('+91')
function setData() {
    contact.value = JSON.parse(localStorage.getItem('ContactDetails'));
    isEmailValid.value = true;
    isMobileValid.value = true;
}

function getDate(selectedDate: string,) { //parsing to isostring
    if (selectedDate == '' || selectedDate == null) return null;
    const specificDate = new Date(selectedDate);
    return specificDate.toISOString();
}

async function onFormSubmit() {
    contact.value.birthDay = getDate(contact.value.birthDay),
        contact.value.anniversary = getDate(contact.value.anniversary)
    console.log(contact.value)
    const resp = await axios.put('/api/contacts/update', contact.value, {
    }).then((resp) => {
        console.log(resp.data)
    })
    setData();
    localStorage.removeItem('ContactDetails');
    router.go(-1)
}

const onFormReset = () => {
    setData();
    localStorage.removeItem('ContactDetails');
    router.go(-1)
}

setData(); //to intialize the fields on opening of Dailog
const genderList = [
    { title: 'Male', value: 'MALE' },
    { title: 'Female', value: 'FEMALE' },
]
function emailValid(email: string) {
    if (isEmpty(email)) {
        isEmailValid.value = false;
        return 'This field is required';
    }
    isEmailValid.value = emailValidator(email);
    if (isEmailValid.value != true) isEmailValid.value = false;
}
function mobileValidator(mobile: string) {
    if (isEmpty(mobile)) {
        isMobileValid.value = false;
        return 'This field is required';
    }
    else {
        const mobileValidObj = phoneValidator(mobile)
        if (mobileValidObj.valid) {
            isMobileValid.value = true;
            contact.value.mobilePhone = mobileValidObj.number //setting the parsed value
            return true;
        }
        isMobileValid.value = false;
        return 'Enter a valid mobile number'
    }

}
function zipValidator(pinCode: string) {
    if (isEmpty(pinCode)) {
        isMobileValid.value = false;
        return 'This field is required';
    }
    const res = validateZip(pinCode)
    if (res.valid) {
        isZipValid.value = true;
        return true;
    }
    else {
        isZipValid.value = false;
        return res.message;
    }
}
</script>

<template>
    <div>
        <VCard v-if="contact" class="pa-sm-8 pa-5">
            <VCardItem class="text-center">
                <VCardTitle class="text-h4 mb-3 font-weight-meduim">
                    Edit Contact Information
                </VCardTitle>
            </VCardItem>

            <VCardText>
                <!-- 👉 Form -->
                <VForm class="mt-6">
                    <VRow>
                        <!-- 👉 First Name -->
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.firstName" label="First Name" type="text" />
                        </VCol>

                        <!-- 👉 Last Name -->
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.lastName" label="Last Name" />
                        </VCol>
                    </VRow>
                    <VRow>
                        <!-- 👉  Email -->
                        <VCol cols="12" md="12">
                            <AppTextField v-model="contact.emailId" label="Email Id"
                                :rules="[emailValid, emailValidator]" />
                        </VCol>
                    </VRow>
                    <VRow>
                        <!-- <VCol cols="2" md="2">
                            <AppTextField v-model="country" label="Mobile" />
                        </VCol> -->

                        <!-- 👉 Contact -->
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.mobilePhone" label="Mobile" :rules="[mobileValidator]" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppSelect v-model="contact.gender" :items="genderList" label="Gender" />
                        </VCol>
                    </VRow>
                    <VRow>
                        <VCol cols="12" md="6">
                            <AppDateTimePicker v-model="contact.birthDay" label="Birthday" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppDateTimePicker v-model="contact.anniversary" label="Anniversary" />
                        </VCol>
                        <VCol cols="12" md="6" class="mt-1">
                            <AppTextarea v-model="contact.addressOne" label="Address 1" />
                        </VCol>
                        <VCol cols="12" md="6" class="mt-1">
                            <AppTextarea v-model="contact.addressTwo" label="Address 2" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.city" label="City" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.state" label="State" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.country" label="Country" />
                        </VCol>
                        <VCol cols="12" md="6">
                            <AppTextField v-model="contact.zip" label="Zip Code" :rules="[zipValidator]" />
                        </VCol>
                        <VCol cols="12" md="6" v-for="(udf, index) in posMappingList" :key="index" class="text-break">
                            <AppDateTimePicker v-model="contact[udf.custFieldName]" :label="udf.displayLabel"
                                v-if="udf.dataType.substring(0, 4) == 'Date'"></AppDateTimePicker>
                            <AppTextField v-else :label="udf.displayLabel" v-model="contact[udf.custFieldName]" />
                        </VCol>
                    </VRow>
                    <VRow>
                        <!-- 👉 Submit and Cancel -->
                        <VCol cols="12" class="d-flex flex-wrap justify-center gap-4">
                            <VBtn :disabled="!(isEmailValid && isMobileValid && isZipValid)" @click="onFormSubmit">
                                Submit
                            </VBtn>
                            <VBtn color="error" variant="tonal" @click="onFormReset">
                                Cancel
                            </VBtn>

                        </VCol>
                    </VRow>
                </VForm>
            </VCardText>
        </VCard>
    </div>
</template>
<route lang="yaml">
    meta:
      action: Read
      subject: Contacts
</route>

