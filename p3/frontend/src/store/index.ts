// store.ts
import { defineStore } from "pinia";

export const useSharedStore = defineStore("shared", {
    state: () => ({
        ReceiptConfiguration: {
            ReceiptVideoEmbed: {
                url: "",
            },
            ReceiptSocialIcons: {
                facebook: "",
                instagram: "",
                twitter: "",
                linkedin: "",
                youtube: "",
            },
            ReceiptItemsList: {
                itemName: ''
            },
            isValid: {
                valid: 'none'
            }
        },
        Loading: {
            isLoading: false,
        },
        snackbar: {
            content: '',
            color: '',
            icon: '',
            isVisible: false
        }
    }),
    actions: {
        setVideoConfig(config: { url: string }) {
            this.ReceiptConfiguration.ReceiptVideoEmbed = config;
        },
        setSocialConfig(config: {
            facebook: string;
            instagram: string;
            twitter: string;
            linkedin: string;
            youtube: string;
        }) {
            this.ReceiptConfiguration.ReceiptSocialIcons = config;
        },
        setReceiptItemNameConfig(config: { itemName: string }) {
            this.ReceiptConfiguration.ReceiptItemsList.itemName = config;
        },
        setReceiptConfigValidation(payload: string) {
            this.ReceiptConfiguration.isValid.valid = payload;
        },
        setLoading(payload: boolean) {
            this.Loading.isLoading = payload;
        },
        setSnackbar(payload: {
            content: string,
            color: string,
            icon: string,
            isVisible: boolean
        }) {
            this.snackbar = payload;
        }
    },
});