<script setup lang="ts">
import { ref } from "vue";
import Rules from "../components/Rules.vue";
import { useRoute } from "vue-router";
import router from "@/router";
import axios from "@axios";

/*GET AND SET COUPON CODE FROM API*/
let coupon_codes = {};
let segment_rule = '';
let interactionRules = {};
let loyaltyTiers = {}
let UDF = {};
let purchaseUDF = {}
/* USE WHEN UPDATE STRING IN RULES */
const ruleTypeKey = [
  [
    "c.country",
    "Date(c.created_date)",
    "c.email_id",
    "c.city",
    "c.state",
    "c.zip",
    "c.gender",
    "c.optin_medium",
    "c.home_store",
    "Date(c.birth_day)",
    "Date(c.anniversary_day)",
    "Date(loyalty.created_date)",
    "loyalty.membership_status",
    "loyalty.total_loyalty_earned",
    "loyalty.total_giftcard_amount",
    "loyalty.loyalty_balance",
    "loyalty.giftcard_balance",
    "loyalty.program_tier_id",
    "c.udf"
  ],
  [
    "Date(sal.sales_date)",
    "aggr_tot",
    "aggr_avg",
    "aggr_count",
    "sal.store_number",
    "sal.subsidiary_number",
    "sal.sku",
    "sku.item_category",
    "sku.department_code",
    "sku.description",
    "sku.class_code",
    "sku.subclass_code",
    "sku.dcs",
    "sku.vendor_code",
    "sal.udf",
    "sku.udf"
  ],
  ["ce.event_type"],
  ["cp.coupon_id", "cd.status", "date(cd.issued_on)", "date(cd.redeemed_on)", "date(cd.expired_on)"]
];

/*RULES TYPE */
const rules_type = [
  {
    id: 1,
    title: "Customer Rules",
    rules: [
      {
        rule_name: ["Country"],
        rule_name_string: ["Country"],
        rule_condition: ["is", "is not"],
        rule_name_key: ["c.country", "c.country"],
        rule_condition_key: ["string", "string"],
        rule_operation: ["Equal to", "One of", "Empty"],
        rule_operation_key: ["text", "text", "none"],
        rule_type: [""],
      },
      {
        rule_name: ["Date Added"],
        rule_name_string: ["Date Added"],
        rule_condition: ["is", "is(in range)", "within last"],
        rule_name_key: [
          "Date(c.created_date)",
          "Date(c.created_date)",
          "Date(c.created_date)",
        ],
        rule_condition_key: ["date", "date", "date"],
        rule_operation: [
          "Today",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
          "Last range of days",
        ],
        rule_operation_key: [
          "today",
          "date",
          "date_range",
          "date",
          "date",
          "date",
          "date",
          "number_range",
        ],
        rule_type: ["days", "weeks", "months"],
      },
      {
        rule_name: ["Email Id"],
        rule_name_string: ["Email Id"],
        rule_condition: ["contains", "doesn't contain", "is empty"],
        rule_name_key: ["c.email_id", "c.email_id", "c.email_id"],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["City"],
        rule_name_string: ["City"],
        rule_condition: ["is", "is not"],
        rule_name_key: ["c.city", "c.city"],
        rule_condition_key: ["string", "string"],
        rule_operation: ["Equal to", "One of", "Empty"],
        rule_operation_key: ["text", "text", "none"],
        rule_type: [""],
      },
      {
        rule_name: ["State"],
        rule_name_string: ["State"],
        rule_condition: ["is", "is not"],
        rule_name_key: ["c.state", "c.state"],
        rule_condition_key: ["string", "string"],
        rule_operation: ["Equal to", "One of", "Empty"],
        rule_operation_key: ["text", "text", "none"],
        rule_type: [""],
      },
      {
        rule_name: ["Zip"],
        rule_name_string: ["Zip"],
        rule_condition: ["is", "is not"],
        rule_name_key: ["c.zip", "c.zip"],
        rule_condition_key: ["number", "number"],
        rule_operation: ["Equal to", "One of", "Empty"],
        rule_operation_key: ["number", "text", "none"],
        rule_type: [""],
      },
      {
        rule_name: ["Gender"],
        rule_name_string: ["Gender"],
        rule_condition: ["is"],
        rule_name_key: ["c.gender"],
        rule_condition_key: ["string"],
        rule_operation: ["Male", "Female", "Others"],
        rule_operation_key: ["option", "option", "option"],
        rule_type: [""],
      },
      {
        rule_name: ["Contact Source"],
        rule_name_string: ["Contact Source"],
        rule_condition: ["is"],
        rule_name_key: ["c.optin_medium"],
        rule_condition_key: ["string"],
        rule_operation: [
          "POS",
          "Manual Upload",
          "Web Form",
          "Mobile Opt-in",
          "eCommerce",
        ],
        rule_operation_key: ["option", "option", "option", "option", "option"],
        rule_type: [""],
      },
      {
        rule_name: ["Home Store"],
        rule_name_string: ["Home Store"],
        rule_condition: ["is"],
        rule_name_key: ["c.home_store"],
        rule_condition_key: ["string"],
        rule_operation: ["Equal to", "Doesn't equal", "One of", "Empty"],
        rule_operation_key: ["text", "text", "text", "none"],
        rule_type: [""],
      },
      {
        rule_name: ["Birthday"],
        rule_name_string: ["Birthday"],
        rule_condition: ["is", "is(year ignored)", "within last"],
        rule_name_key: [
          "Date(c.birth_day)",
          "Date(c.birth_day)",
          "Date(c.birth_day)",
        ],
        rule_condition_key: ["date", "date", "date"],
        rule_operation: [
          "Last range of days",
          "Next Range Of Days",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
          "Empty",
        ],
        rule_operation_key: [
          "number_range",
          "number_range",
          "date",
          "date_range",
          "date",
          "date",
          "date",
          "date",
          "none",
        ],
        rule_type: ["days", "weeks", "months"],
      },
      {
        rule_name: ["Anniversary"],
        rule_name_string: ["Anniversary"],
        rule_condition: ["is", "is(year ignored)", "within last"],
        rule_name_key: [
          "Date(c.anniversary_day)",
          "Date(c.anniversary_day)",
          "Date(c.anniversary_day)",
        ],
        rule_condition_key: ["date", "date", "date"],
        rule_operation: [
          "Last range of days",
          "Next Range Of Days",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
          "Empty",
        ],
        rule_operation_key: [
          "number_range",
          "number_range",
          "date",
          "date_range",
          "date",
          "date",
          "date",
          "date",
          "none",
        ],
        rule_type: ["days", "weeks", "months"],
      },
      {
        rule_name: ["Loyalty Enrolled Date"],
        rule_name_string: ["Loyalty Opt-in Date"],
        rule_condition: ["is", "is(in range)", "within last"],
        rule_name_key: [
          "Date(loyalty.created_date)",
          "Date(loyalty.created_date)",
          "Date(loyalty.created_date)",
        ],
        rule_condition_key: ["date", "date", "date"],
        rule_operation: [
          "Today",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
          "Last range of days",
        ],
        rule_operation_key: [
          "today",
          "date",
          "date_range",
          "date",
          "date",
          "date",
          "date",
          "number_range",
        ],
        rule_type: ["days", "weeks", "months"],
      },
      {
        rule_name: ["Membership Status"],
        rule_name_string: ["Membership Status"],
        rule_condition: ["is"],
        rule_name_key: ["loyalty.membership_status"],
        rule_condition_key: ["string"],
        rule_operation: ["Active", "Expired", "Suspended"],
        rule_operation_key: ["option", "option", "option"],
        rule_type: [""],
      },
      {
        rule_name: ["Loyalty Earned"],
        rule_name_string: ["Loyalty Balance"],
        rule_condition: ["Total", "Balance"],
        rule_name_key: [
          "loyalty.total_loyalty_earned",
          "loyalty.total_giftcard_amount",
          "loyalty.loyalty_balance",
          "loyalty.giftcard_balance",
        ],
        rule_condition_key: ["number", "number"],
        rule_operation: ["Points", "Amount"],
        rule_operation_key: ["number", "number"],
        rule_type: ["More than", "Less than", "Equal to", "Between", "Empty"],
      },
      {
        rule_name: ["Loyalty Tier"],
        rule_name_string: ["Loyalty Tier"],
        rule_condition: ["is"],
        rule_name_key: ["loyalty.program_tier_id"],
        rule_condition_key: [],
        rule_operation: Object.keys(loyaltyTiers),
        rule_operation_key: ["Number:="],
        rule_type: [""],
      },
      {
        rule_name: ["Others"],
        rule_name_string: ["C.UDF"],
        rule_condition: Object.keys(UDF),
        rule_name_key: ["c.udf1"],
        rule_condition_key: ["string"],
        rule_operation: ["Equal to", "Doesn't equal", "One of", "contains", "doesn't contain", "Starts with", "Ends with", "Is empty"],
        rule_operation_key: ["text", "text", "text", "text", "text", "text", "text", "none"],
        rule_type: [""],
      },
    ],
  },
  {
    id: 2, title: "Interaction Rules", rules: [
      {
        rule_name: ["Interaction-Rules"],
        rule_name_string: ["ce.event_type"],
        rule_condition: Object.values(interactionRules),
        rule_name_key: ["ce.event_type"],
        rule_condition_key: ["date", "date", "date"],
        //rule_condition2: Object.keys(coupon_codes),
        rule_operation: ["Received", "Opened", "Clicked"],
        rule_operation_key: ["string", "string"],
        rule_type: ["Yes", "No"],
      }
    ]
  },
  {
    id: 3,
    title: "Purchase Rules",
    rules: [
      {
        rule_name: ["Purchase Date"],
        rule_name_string: ["Purchase Date"],
        rule_condition: [
          "is",
          "is(in range)",
          "within last",
          "not within last",
          "is not",
          "days since last purchase",
        ],
        rule_name_key: [
          "Date(sal.sales_date)",
          "Date(sal.sales_date)",
          "Date(sal.sales_date)",
          "Date(sal.sales_date)",
          "Date(sal.sales_date)",
          "Date(sal.sales_date)",
        ],
        rule_condition_key: ["date", "date", "date", "date", "date", "number"],
        rule_operation: [
          "Today",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
          "Last range of days",
        ],
        rule_operation_key: [
          "today",
          "date",
          "date_range",
          "date",
          "date",
          "date",
          "date",
          "number_range",
        ],
        rule_type: ["days", "weeks", "months"],
      },
      {
        rule_name: ["Purchase Amount"],
        rule_name_string: ["Total Purchase Amount"],
        rule_condition: ["Total", "Average of all"],
        rule_name_key: ["aggr_tot", "aggr_avg"],
        rule_condition_key: ["number", "number"],
        rule_operation: ["is more than", "is less than", "is in the range of"],
        rule_operation_key: ["number", "number", "number_range"],
        rule_type: [""],
      },
      {
        rule_name: ["Number of Purchases"],
        rule_name_string: ["Total Number of Purchases"],
        rule_condition: ["Total"],
        rule_name_key: ["aggr_count"],
        rule_condition_key: ["number"],
        rule_operation: ["is more than", "is less than", "is in the range of"],
        rule_operation_key: ["number", "number", "number_range"],
        rule_type: [""],
      },
      {
        rule_name: ["Store Number"],
        rule_name_string: ["Store Number"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: [
          "sal.store_number",
          "sal.store_number",
          "sal.store_number",
        ],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Subsidiary Number"],
        rule_name_string: ["Subsidiary Number"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: [
          "sal.subsidiary_number",
          "sal.subsidiary_number",
          "sal.subsidiary_number",
        ],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["SKU"],
        rule_name_string: ["SKU"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: ["sal.sku", "sal.sku", "sal.sku"],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Item Category"],
        rule_name_string: ["Item Category"],
        rule_condition: [
          "Equals",
          "Doesn't equal",
          "One of",
          "Contains",
          "Doesn't Contain",
          "Only like",
        ],
        rule_name_key: [
          "sku.item_category",
          "sku.item_category",
          "sku.item_category",
          "sku.item_category",
          "sku.item_category",
          "sku.item_category",
        ],
        rule_condition_key: [
          "string",
          "string",
          "string",
          "string",
          "string",
          "string",
        ],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Department"],
        rule_name_string: ["Department"],
        rule_condition: ["Equals", "Doesn't equal", "One of", "Only like"],
        rule_name_key: [
          "sku.department_code",
          "sku.department_code",
          "sku.department_code",
          "sku.department_code",
        ],
        rule_condition_key: ["string", "string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Description"],
        rule_name_string: ["Description"],
        rule_condition: [
          "Equals",
          "Doesn't equal",
          "One of",
          "Contains",
          "Doesn't Contain",
        ],
        rule_name_key: [
          "sku.description",
          "sku.description",
          "sku.description",
          "sku.description",
          "sku.description",
        ],
        rule_condition_key: ["string", "string", "string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Class"],
        rule_name_string: ["Class"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: ["sku.class_code", "sku.class_code", "sku.class_code"],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Sub Class"],
        rule_name_string: ["Subclass"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: [
          "sku.subclass_code",
          "sku.subclass_code",
          "sku.subclass_code",
        ],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["DCS"],
        rule_name_string: ["DCS"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: ["sku.dcs", "sku.dcs", "sku.dcs"],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Vendor"],
        rule_name_string: ["Vendor Code"],
        rule_condition: ["Equals", "Doesn't equal", "One of"],
        rule_name_key: [
          "sku.vendor_code",
          "sku.vendor_code",
          "sku.vendor_code",
        ],
        rule_condition_key: ["string", "string", "string"],
        rule_operation: [],
        rule_operation_key: ["text"],
        rule_type: [""],
      },
      {
        rule_name: ["Others"],
        rule_name_string: ["P.UDF"],
        rule_condition: Object.keys(purchaseUDF),
        rule_name_key: ["sal.udf"],
        rule_condition_key: ["string"],
        rule_operation: ["Equal to", "Doesn't equal", "One of", "contains", "doesn't contain", "Starts with", "Ends with", "Is empty"],
        rule_operation_key: ["text", "text", "text", "text", "text", "text", "text", "none"],
        rule_type: [""],
      },
    ],
  },
  {
    id: 4,
    title: "Coupon Rules",
    rules: [
      {
        rule_name: ["Discount-code Date"],
        rule_name_string: ["Promo-code Date"],
        rule_condition: ["Issued on", "Redeemed on", "Expiry on",],
        rule_name_key: ["date(cd.issued_on)", "date(cd.redeemed_on)", "date(cd.expired_on)", "cd.status"],
        rule_condition_key: ["date", "date", "date"],
        rule_condition2: Object.keys(coupon_codes),
        rule_operation: ["is", "within last", "not within last", "is (in range)", "is in next", "any date", "never",],
        rule_operation_key: ["date_last", "number_with", "number_with", "number_range", "number_with", "none", "none"],
        rule_type: ["Today", "Equal to", "Between", "On or after", "On or before", "After", "Before"],
      }
    ]
  },
];

const activeButtonIndex = ref([]);
const expansionPanels = ref([]);
const is_segment_save = ref(false);
const isScroll = ref(false);
const selectedOrSegment = ref([]);
const selectedAndSegment = ref([]);
const screenHeight = ref(false);
const orSegment = ref([]);
const andOrSegment = ref([]);
const showError = ref(false);
const valid = ref('');
const route = useRoute();

//const timeout = 2000
//const text ="Some conditions are missing required values."

/*ADD NEW RULE TYPE PANEL*/
function addRule(index) {
  if (!expansionPanels.value.some((item) => item.index === index)) {
    expansionPanels.value.push({
      index: index,
      title: rules_type[index].title,
      id: rules_type[index].id,
      segmentRules: rules_type[index].rules,
    });
    /*FOR ACTIVE RULSE TYPE BUTTON*/
    activeButtonIndex.value.push(index);
  }
}

const totSize = ref(0);
const totEmailSize = ref(0);
const totMobileSize = ref(0);
const refresh = ref(false);
const segmentName = ref("");
const segmentId = ref("");
const description = ref("");
const segmentRule = ref("");
const openDialogBox = ref(false);
const dialogBoxTitle = ref("");
const yesBtnEnable = ref(true);
const noBtnText = ref("");
const segmentNameTextBoxIsDisabled = ref(false);
const editedDiscountedCode = ref('')
const editedTier = ref([]);
const conditionsAndOR = ref([]);
const conditionsOR = ref(0);
const editedUDF = ref([]);
const editedPurchaseUDF = ref([]);
/* GENRATE STRING ON SAVE SEGMENT */
function generateSegmentRule() {
  segmentRule.value = "";
  // is_segment_save.value = true;
  /*SORTING FROM OR INDEX AND RULE ID*/

  const get_segment_or_rules = selectedOrSegment.value.sort((a, b) => {
    if (a.or_index !== b.or_index) {
      return a.or_index - b.or_index;
    } else {
      return a.rule_id - b.rule_id;
    }
  });
  /*SORTING FROM AND OR INDEX AND RULE ID*/
  const get_segment_and_rules = selectedAndSegment.value.sort((a, b) => {
    if (a.and_con_index !== b.and_con_index) {
      return a.and_con_index - b.and_con_index;
    } else {
      return a.rule_id - b.rule_id;
    }
  });
  /*MERGE OR RULE WITH AND RULE */
  const mergedRules = [...get_segment_or_rules, ...get_segment_and_rules];

  // Sort the merged array based on the rule_id
  const sortedMergedRules = mergedRules.sort((a, b) => a.rule_id - b.rule_id);
  updateRuleConditions(sortedMergedRules);
  let prevRuleId = null;
  let prevAndConIndex = null;
  let formattedString = sortedMergedRules
    .map((rule) => {
      let ruleString = rule.values
        .map((innerRule) => {
          const parts = [];
          innerRule.rule_name != 'C.UDF' && innerRule.rule_name != 'P.UDF' ? parts.push(innerRule.rule_name) : parts.push(innerRule.rule_condition)
          parts.push(innerRule.rule_name_key)
          innerRule.rule_name != 'C.UDF' && innerRule.rule_name != 'P.UDF' ? parts.push(
            `${innerRule.rule_condition_key}:${innerRule.rule_condition}`
          ) : parts.push(
            `${innerRule.rule_condition_key}:${getComparisonString(innerRule.rule_operation)}`)
          parts.push(innerRule.rule_string_value);
          return parts.join("|");
        })
        .join("<OR>");

      const separator =
        (prevAndConIndex !== null && prevAndConIndex !== rule.and_con_index) ||
          (prevRuleId !== null && prevRuleId !== rule.rule_id)
          ? "||"
          : "";
      const additionalConditions: string[] = [];
      // Add additional conditions based on innerRule.rule_name_key of COUPON RULES
      rule.values.forEach((innerRule) => {
        if (innerRule.rule_name === 'Promo-code Date') {
          additionalConditions.push(`OptCulture Promotion Name|cd.coupon_id|String:is|${coupon_codes[innerRule.rule_condition2] == null ? innerRule.rule_condition2 : coupon_codes[innerRule.rule_condition2]}<OR>||OptCulture Promotion Name|cp.coupon_id|String:is|${coupon_codes[innerRule.rule_condition2] == null ? innerRule.rule_condition2 : coupon_codes[innerRule.rule_condition2]}<OR>||`);
        }

        if (innerRule.rule_name_key == 'date(cd.issued_on)') {
          if (innerRule.rule_operation == 'any date') {
            additionalConditions.push(`Promo-code Status|cd.status|String:is value| IN('Active','Redeemed')`);
            ruleString = []
          } else if (innerRule.rule_operation == 'never') {
            additionalConditions.push(`Promo-code Status|cd.status|String:is not in active|notactive`);
            ruleString = []
          } else {
            additionalConditions.push(`Promo-code Date|cd.status|string:is issued|Active<OR>||`);
          }
        } else if (innerRule.rule_name_key == 'date(cd.redeemed_on)') {
          if (innerRule.rule_operation === 'any date') {
            additionalConditions.push(`Promo-code Status|cd.status|String:is in redeem|Redeemed`);
            ruleString = []

          } else if (innerRule.rule_operation == 'never') {
            additionalConditions.push(`Promo-code Status|cd.status|String:is not in redeem|Active`);
            ruleString = []
          } else {
            additionalConditions.push(`Promo-code Date|cd.status|string:is redeemed|Redeemed<OR>||`);
          }
        } else if (innerRule.rule_name_key == 'date(cd.expired_on)') {
          additionalConditions.push(`Promo-code Date|cd.status|string:is expired|Active','Expired<OR>||`);
        } else if (innerRule.rule_name_key == 'ce.event_type' || innerRule.rule_name == 'ce.event_type') {
          if (innerRule.rule_operation == 'Received') {
            if (innerRule.rule_time_period == 'Yes') {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is|'Delivered'<OR>`)
            } else {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is not|'Delivered'<OR>`)
            }
          }
          else if (innerRule.rule_operation == 'Opened') {
            if (innerRule.rule_time_period == 'Yes') {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is in|('Read', 'Clicked')<OR>`)
            } else {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is not in|('Read', 'Clicked')<OR>`)
            }
          }
          else if (innerRule.rule_operation == 'Clicked') {
            if (innerRule.rule_time_period == 'Yes') {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is|'Clicked'<OR>`)
            } else {
              additionalConditions.push(`${innerRule.rule_condition}|ce.event_type|string:is not|'Clicked'<OR>`)
            }
          }
        }
      });
      prevAndConIndex = rule.and_con_index;
      prevRuleId = rule.rule_id;
      return rule.rule_id === 2 ? `${separator}${additionalConditions.join('')}` : `${separator}${additionalConditions.join('')}${ruleString}<OR>`;

    })
    .join("");
  const validString = validateGenrateString(formattedString)
  if (!validString) {
    showError.value = true
  } else {
    if (formattedString.includes('Loyalty Tier')) {
      let regex = /Number:=([^<]+)<OR>/g; // Added 'g' flag for global search

      let match;
      while ((match = regex.exec(formattedString)) !== null) {
        let oldValue = match[1];
        let newValue = loyaltyTiers[oldValue];
        let modifiedSegmentValue = formattedString.replace(`Number:=${oldValue}`, `Number:=|${newValue}`);
        let modifiedString = modifiedSegmentValue.replace(`Loyalty Tier||`, 'Loyalty Tier|');
        modifiedString = modifiedString.replace('loyalty.program_tier_id:null|', 'loyalty.program_tier_id|');
        modifiedString = modifiedString.replace('|undefined:is', '');
        modifiedString = modifiedString.replace('loyalty.program_tier_id:is|', '');
        formattedString = modifiedString;
      }
    }
    console.log("rulese_formate", formattedString)
    segmentRule.value = formattedString;
  }
}
function refreshSegmentCounts() {
  refresh.value = true;
  generateSegmentRule();
  const isAllFieldsFilled = ref(true);

  if (selectedOrSegment.value.length != 0) {
    // if (selectedOrSegment.value.length != conditionsOR.value - 1) {
    //   isAllFieldsFilled.value = false;
    // } else {
    selectedOrSegment.value.forEach(orSegmentArray => {
      orSegmentArray.values.forEach(orSegment => {
        console.log("Or-SEGMENt :: ", orSegment, isAllFieldsFilled.value, orSegment.rule_operation_key)
        if (orSegment.rule_string_value == "" || orSegment.rule_name == '' || orSegment.rule_string_value == null || orSegment.rule_string_value < 0) {
          isAllFieldsFilled.value = false;
        }

        if (orSegment.rule_string_value && orSegment.rule_operation_key == "number_range" && orSegment.rule_string_value.includes('|')) {
          let check = orSegment.rule_string_value.split('|').every(val => {
            if (!isNaN(val)) {
              return Number(val) > 0;
            } else {
              return true;
            }
          });
          check ? '' : isAllFieldsFilled.value = false;
        }
      })
    })
  }

  if (selectedAndSegment.value.length != 0) {

    selectedAndSegment.value.forEach(andSegmentArray => {
      andSegmentArray.values.forEach(andSegment => {
        console.log("And-SEGMENt :: ", andSegment.rule_string_value, andSegment, conditionsAndOR.value, valid.value, andSegment.rule_name, isAllFieldsFilled.value)
        if (andSegment.rule_name_key == "ce.event_type" || andSegment.rule_name == "ce.event_type") {
          if (valid.value != 'success') {
            isAllFieldsFilled.value = false;
          }
        }
        else if (andSegment.rule_string_value && andSegment.rule_operation_key == "number_range" && andSegment.rule_string_value.includes('|')) {
          let check = andSegment.rule_string_value.split('|').every(val => {
            if (!isNaN(val)) {
              return Number(val) > 0;
            } else {
              return true;
            }
          });
          check ? '' : isAllFieldsFilled.value = false;
        }
        else {
          if (andSegment.rule_string_value == "" || andSegment.rule_name == '' || andSegment.rule_string_value == null || andSegment.rule_string_value < 0) {
            console.log("And:: ", andSegment.rule_string_value, valid.value, andSegment.rule_name)
            isAllFieldsFilled.value = false;
          }
        }
      })
    });
  }


  console.log("isAllFieldsFilled :: ", isAllFieldsFilled.value);

  if (!isAllFieldsFilled.value) {
    openDialogBox.value = true;
    dialogBoxTitle.value = "Please provide values for selected rule(s)";

    yesBtnEnable.value = false;
    noBtnText.value = "OK";
    refresh.value = false;
  }
  else if (segmentRule.value == "") {
    openDialogBox.value = true;
    dialogBoxTitle.value = "Please add at least one rule";

    yesBtnEnable.value = false;
    noBtnText.value = "OK";
    refresh.value = false;

    totSize.value = 0;
    totEmailSize.value = 0;
    totMobileSize.value = 0;
  } else {

    const data = {
      segmentRule: segmentRule.value,
    };

    axios.post("/api/segment/get-segment-data", data).then((response) => {
      totSize.value = response.data.totSize;
      totEmailSize.value = response.data.totEmailSize;
      totMobileSize.value = response.data.totMobileSize;
      refresh.value = false;
    });
  }
}

function validateSegmentBeforeSaving() {
  const isAllFieldsFilled = ref(true);
  generateSegmentRule();
  openDialogBox.value = true;

  // if (selectedOrSegment.value.length != 0) {
  //   if (selectedOrSegment.value.length != conditionsOR.value - 1) {
  //     isAllFieldsFilled.value = false;
  //   } else {
  if (selectedOrSegment.value.length != 0) {
    // if (selectedOrSegment.value.length != conditionsOR.value - 1) {
    //   isAllFieldsFilled.value = false;
    // } else {
    selectedOrSegment.value.forEach(orSegmentArray => {
      orSegmentArray.values.forEach(orSegment => {
        console.log("Or-SEGMENt :: ", orSegment, isAllFieldsFilled.value, orSegment.rule_operation_key)
        if (orSegment.rule_string_value == "" || orSegment.rule_name == '' || orSegment.rule_string_value == null || orSegment.rule_string_value < 0) {
          isAllFieldsFilled.value = false;
        }

        if (orSegment.rule_string_value && orSegment.rule_operation_key == "number_range" && orSegment.rule_string_value.includes('|')) {
          let check = orSegment.rule_string_value.split('|').every(val => {
            if (!isNaN(val)) {
              return Number(val) > 0;
            } else {
              return true;
            }
          });
          check ? '' : isAllFieldsFilled.value = false;
        }
      })
    })
    //   }
  }
  if (selectedAndSegment.value.length != 0) {

    selectedAndSegment.value.forEach(andSegmentArray => {
      andSegmentArray.values.forEach(andSegment => {
        console.log("And-SEGMENt :: ", andSegment.rule_string_value, andSegment, conditionsAndOR.value, valid.value, andSegment.rule_name, isAllFieldsFilled.value)
        if (andSegment.rule_name_key == "ce.event_type" || andSegment.rule_name == "ce.event_type") {
          if (valid.value != "success") {
            isAllFieldsFilled.value = false;
          }
        }
        else if (andSegment.rule_string_value && andSegment.rule_operation_key == "number_range" && andSegment.rule_string_value.includes('|')) {
          let check = andSegment.rule_string_value.split('|').every(val => {
            if (!isNaN(val)) {
              return Number(val) > 0;
            } else {
              return true;
            }
          });
          check ? '' : isAllFieldsFilled.value = false;
        }
        else {
          if (andSegment.rule_string_value == "" || andSegment.rule_name == '' || andSegment.rule_string_value == null || andSegment.rule_string_value < 0 || valid.value != 'success') {
            console.log("And:: ", andSegment.rule_string_value, valid.value, andSegment.rule_name)
            isAllFieldsFilled.value = false;
          }
        }
      })
    });
  }
  //  }
  if (segmentName.value != "" && segmentRule.value != "" && isAllFieldsFilled.value != false) {
    if (segmentNameTextBoxIsDisabled.value)
      dialogBoxTitle.value = "Are you sure you want to update segment ?";
    else
      dialogBoxTitle.value = "Are you sure you want to save segment ?";

    yesBtnEnable.value = true;
    noBtnText.value = "No";
  } else {
    if (segmentName.value == "") {
      dialogBoxTitle.value = "Please provide segment name";
    } else if (segmentRule.value == "") {
      dialogBoxTitle.value = "Please add at least one rule";
    } else if (isAllFieldsFilled.value == false) {
      dialogBoxTitle.value = "Please provide values for selected rule(s)";
    }
    yesBtnEnable.value = false;
    noBtnText.value = "OK";
  }
}

const isSegmentSaved = ref(false);
function saveSegment() {
  openDialogBox.value = false;
  if (segmentRule.value.includes('loyalty.program_tier_id') || segmentRule.value.includes('Loyalty Tier')) {
    let indexToRemove;
    let extractedString;
  }

  const data = {
    segRuleId: segmentId.value,
    segmentName: segmentName.value,
    description: description.value,
    segmentRule: segmentRule.value,
  };

  axios.post("/api/segment/save-segment", data).then((response) => {
    console.log("saving >>>>>>>>>> ", response.data);
    isSegmentSaved.value = true;
    openDialogBox.value = true;
    dialogBoxTitle.value = response.data + " Successfully!";
    yesBtnEnable.value = false;
    noBtnText.value = "OK";

  });
}

function loadActiveDiscounts() {
  axios.get("/api/segment/coupon-names").then((response) => {
    console.log("response :: ", response);
    const couponCodes = response.data;
    rules_type[3].rules[0].rule_condition2 = Object.keys(couponCodes);
    coupon_codes = couponCodes;
    const regex = /(?:cp|cd)\.coupon_id\|String:is\|([^<]+)<OR>/;
    const match = segment_rule.match(regex);
    const couponValue = match ? match[1] : null;
    console.log("Coupon value:", couponValue, coupon_codes, segment_rule);
    editedDiscountedCode.value = discountCode(couponValue, coupon_codes);
  })
}

function InteractionName(value: string | any[], object: { [x: string]: any; }) {
  let matchedKeys: string[] = [];
  value.forEach((value: any) => {
    const matchedKey = Object.keys(object).find(key => object[key] === value);
    if (matchedKey) {
      matchedKeys.push(matchedKey);
    }
  });
  return matchedKeys.length > 0 ? matchedKeys : null;
}

function discountCode(value: string | null, object: { [x: string]: any; }) {
  return Object.keys(object).find(key => object[key] === value) || null;
}

function loadCampaigns() {
  axios.get("/api/segment/campaign-names").then((response) => {
    console.log("response :: ", response);
    const campaigns = response.data;
    rules_type[1].rules[0].rule_condition = Object.values(campaigns)
    const campaignIds = Object.keys(campaigns);
    let campaignsArray = Object.keys(campaigns).map(key => ({
      key: key,
      value: campaigns[key]
    }));
    localStorage.setItem('campaignId', JSON.stringify(campaignsArray));
    interactionRules = campaigns;
    const pattern = /all:87\|\|([^|]*)\|ce\.event/g;
    const matches = [];
    let match;

    while ((match = pattern.exec(segment_rule)) !== null) {
      const matchedString = match[1].trim(); // Get the matched string and trim whitespace
      const separatedStrings = matchedString.split(','); // Split the matched string based on the comma
      matches.push(...separatedStrings);
    }
  })
}
function loadLoyaltyTier() {
  axios.get("/api/segment/lty-prg-tiers").then((response) => {
    console.log("response :: ", response);
    loyaltyTiers = response.data;
    const loyaltyTierIndex = rules_type[0].rules.findIndex(rule => rule.rule_name.includes("Loyalty Tier"));
    if (loyaltyTierIndex !== -1) {
      rules_type[0].rules[loyaltyTierIndex].rule_operation = Object.keys(loyaltyTiers);
    }
    let regex = /Number:=\|(\d+)<OR>/g; // Added 'g' flag for global search

    let match;
    while ((match = regex.exec(segment_rule)) !== null) {
      let tierNumber;
      let tierKey;

      if (match && match[1]) {
        tierNumber = match[1];
      } else {
        console.log("No match found.");
        continue;
      }

      for (let key in loyaltyTiers) {
        if (loyaltyTiers[key] == tierNumber) {
          tierKey = key;
          break;
        }
      }
      if (tierKey != null) {
        editedTier.value.push(tierKey);
      }
    }
  })
}

function loadUDF() {
  axios.get("/api/segment/udfs").then((response) => {
    console.log("response :: ", response);
    UDF = response.data[0];
    purchaseUDF = { ...response.data[1], ...response.data[2] };
    localStorage.setItem('customer-udfs', JSON.stringify(UDF))
    localStorage.setItem('purchase-udfs', JSON.stringify(purchaseUDF))

    const udfIndex = rules_type[0].rules.findIndex(rule => rule.rule_name_string.includes("C.UDF"));
    const purchaseUdfIndex = rules_type[2].rules.findIndex(rule => rule.rule_name_string.includes("P.UDF"));

    if (udfIndex != -1) {
      rules_type[0].rules[udfIndex].rule_condition = Object.keys(UDF);
    }
    console.log('purchaseUdfIndex', purchaseUdfIndex)
    if (purchaseUdfIndex != -1) {
      rules_type[2].rules[purchaseUdfIndex].rule_condition = Object.keys(purchaseUDF);
    }

    console.log('udfIndex', udfIndex)
    let regex = /c\.udf\d+(?=\|)/g;
    let Pregex = /sal\.udf\d+(?=\|)/g;
    let Sregex = /sku\.udf\d+(?=\|)/g;
    processSegmentRule(segment_rule, Pregex); // Process using Pregex
    processSegmentRule(segment_rule, Sregex); // Process using Sregex

    let match;
    while ((match = regex.exec(segment_rule)) !== null) {
      let udfKey = match[0]; // Access the entire matched substring
      console.log(' ', udfKey);
    }
    console.log('segment_rule', segment_rule)
    while ((match = regex.exec(segment_rule)) != null) {
      let udfNumber;
      let udfKey;
      console.log('match', match, match[0])
      if (match && match[0]) {
        udfNumber = match[0];
        console.log('udfNumber', udfNumber)
      } else {
        console.log("No match found.");
        continue;
      }

      for (let [key, value] of Object.entries(UDF)) {
        if (value == udfNumber) {
          udfKey = key;
          console.log('udfKey', udfKey)
          break;
        }
      }

      if (udfKey != null) {
        editedUDF.value.push(udfKey);
      }
    }
  })
  console.log('editedUDF', editedUDF.value, UDF)
}

// Function to process segment_rule using a given regex
function processSegmentRule(segment_rule, regex) {
  let matches = segment_rule.match(regex);
  console.log('matches', matches)
  if (matches) {
    for (let udfNumber of matches) {
      let udfKey = null;

      // Find the corresponding udfKey from the UDF object
      for (let [key, value] of Object.entries(purchaseUDF)) {
        if (value == udfNumber) {
          udfKey = key;
          console.log('udfKey', udfKey)
          break;
        }
      }

      if (udfKey != null) {
        editedPurchaseUDF.value.push(udfKey);
        console.log('editedPurchaseUDF', editedPurchaseUDF.value)
      }
    }
  }
}

function getComparisonString(comparisonType) {
  switch (comparisonType) {
    case "Equal to":
      return 'is';
    case "Doesn't equal":
      return 'is not';
    case "One of":
      return 'is in';
    case "contains":
      return 'contains';
    case "doesn't contain":
      return 'does not contain';
    case "Starts with":
      return 'starts with';
    case "Ends with":
      return 'ends with';
    case "Is empty":
      return 'is empty';
    default:
      return 'Invalid comparison type';
  }
}

interactionRules = {}
loadActiveDiscounts();
loadLoyaltyTier()
loadCampaigns()
function closeDialogBox() {
  openDialogBox.value = false;
  if (isSegmentSaved.value && !openDialogBox.value)
    router.push("/segments")
}

/* VALIDATE STRING HAVE VALUE */
function validateGenrateString(queryString) {
  queryString = queryString.replace(/null/g, '');
  const conditions = queryString.split('|<OR>');
  if (conditions.length >= 1) {
    return queryString
  }
  return false
}

/* UPDATE RULE AS PER RULES OPERATION AS PER STRING*/
function updateRuleConditions(rules) {
  rules.forEach((rule) => {
    rule.values.forEach((value) => {
      /*RULE CONDITION NAME CHANGE*/
      if (
        (value.rule_condition === "is" && value.rule_operation === "Empty") ||
        value.rule_condition === "is empty" ||
        value.rule_time_period === "Empty"
      ) {
        value.rule_condition = "none";
      } else if (value.rule_operation_key === "text") {
        if (
          value.rule_condition === "is" &&
          value.rule_operation === "One of"
        ) {
          value.rule_condition = "is in";
        } else if (
          value.rule_operation === "One of" &&
          value.rule_condition === "is not"
        ) {
          value.rule_condition = "is not in";
        } else if (value.rule_operation === "After") {
          value.rule_condition = "notafter";
        } else if (value.rule_operation === "Before") {
          value.rule_condition = "notbefore";
        } else if (value.rule_operation == "Doesn't equal" && value.rule_name != 'C.UDF' && value.rule_name != 'P.UDF') {
          value.rule_condition = "is not";
        }
      } else if (value.rule_operation_key === "number") {
        if (
          value.rule_condition == "is" &&
          value.rule_operation === "Equal to"
        ) {
          value.rule_condition = "=";
        } else if (
          value.rule_condition == "is not" &&
          value.rule_operation === "Equal to"
        ) {
          value.rule_condition = "!=";
        } else if (
          value.rule_condition == "is" &&
          value.rule_operation === "One of"
        ) {
          value.rule_condition = "is in";
        } else if (
          value.rule_operation === "One of" &&
          value.rule_condition === "is not"
        ) {
          value.rule_condition = "is not in";
        } else if (value.rule_operation === "is more than") {
          value.rule_condition = ">";
        } else if (value.rule_operation === "is less than") {
          value.rule_condition = "<";
        }
        /**FOR Loyalty Earned */
        if (
          value.rule_time_period == "More than" &&
          ["Points", "Amount"].includes(value.rule_operation)
        ) {
          value.rule_condition = ">";
        } else if (
          value.rule_time_period == "Less than" &&
          ["Points", "Amount"].includes(value.rule_operation)
        ) {
          value.rule_condition = "<";
        } else if (
          value.rule_time_period == "Equal to" &&
          ["Points", "Amount"].includes(value.rule_operation)
        ) {
          value.rule_condition = "=";
        } else if (
          value.rule_time_period == "Between" &&
          ["Points", "Amount"].includes(value.rule_operation)
        ) {
          value.rule_condition = "between";
        }
      } else if (
        value.rule_operation_key === "number_range" ||
        value.rule_operation_key === "date_range"
      ) {
        if (
          ["Date(c.anniversary_day)", "Date(c.birth_day)"].includes(
            value.rule_name_key
          ) &&
          value.rule_operation === "Last range of days"
        ) {
          value.rule_condition = "before_between";
        } else if (
          value.rule_condition === "is(year ignored)" &&
          value.rule_operation == "Between"
        ) {
          value.rule_condition = "ignoreYear_between";
        } else if (value.rule_operation === "Next Range Of Days") {
          value.rule_condition = "after_between";
        } else if (value.rule_operation === "Last range of days" || value.rule_time_period === 'Last range of days') {
          value.rule_condition = "range_between";
        } else {
          value.rule_condition = "between";
        }
      } else if (value.rule_operation_key == 'date_last') {
        if (value.rule_time_period === "Today" &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)) {
          value.rule_condition = "isToday";
        } else if (['Equal to'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)) {
          value.rule_condition = "equal to";
        } else if (['Between'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "between";
        } else if (['On or after'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "onOrAfter";
        } else if (['On or before'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "onOrBefore";
        } else if (['After'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "after";
        } else if (['Before'].includes(value.rule_time_period) &&
          ['Issued on', 'Redeemed on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "before";
        } else if (value.rule_time_period === "Today" &&
          ['Expiry on'].includes(value.rule_condition)) {
          value.rule_condition = 'isetoday'
        } else if (['Equal to'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)) {
          value.rule_condition = "isequal";
        } else if (['Between'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "isebetween";
        } else if (['On or after'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "iseonOrAfter";
        } else if (['On or before'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "iseonOrBefore";
        } else if (['After'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "iseafter";
        } else if (['Before'].includes(value.rule_time_period) &&
          ['Expiry on'].includes(value.rule_condition)
        ) {
          value.rule_condition = "isebefore";
        }

      } else if (value.rule_condition === "is(year ignored)") {
        if (value.rule_operation == "Equal to") {
          value.rule_condition = "ignoreYear_is";
        } else if (value.rule_operation == "On or after") {
          value.rule_condition = "ignoreYear_onOrAfter";
        } else if (value.rule_operation == "On or before") {
          value.rule_condition = "ignoreYear_onOrBefore";
        } else if (value.rule_operation == "After") {
          value.rule_condition = "ignoreYear_after";
        } else if (value.rule_operation == "Before") {
          value.rule_condition = "ignoreYear_before";
        }
      } else if (value.rule_operation === "On or after") {
        value.rule_condition = "onOrAfter";
      } else if (value.rule_operation === "On or before") {
        value.rule_condition = "onOrBefore";
      } else if (value.rule_operation === "After") {
        if (value.rule_condition == "is not") {
          value.rule_condition = "notafter";
        } else {
          value.rule_condition = "after";
        }
      } else if (value.rule_operation === "Before") {
        if (value.rule_condition == "is not") {
          value.rule_condition = "notbefore";
        } else {
          value.rule_condition = "before";
        }
      } else if (value.rule_condition === "within last" || value.rule_operation == 'within last') {
        value.rule_condition = `withinlast_${value.rule_time_period}`;
      } else if (value.rule_condition === "not within last" || value.rule_operation == 'not within last') {
        value.rule_condition = `notwithinlast_${value.rule_time_period}`;
      } else if (value.rule_condition === "days since last purchase") {
        value.rule_condition = "date_diff_equal_to";
      } else if (value.rule_operation == 'is in next') {
        value.rule_condition = `iswithinnext_${value.rule_time_period}`;
      } else if (
        value.rule_condition === "Total" ||
        value.rule_condition === "Average of all"
      ) {
        if (value.rule_operation === "is more than") {
          value.rule_condition = ">";
        } else if (value.rule_operation === "is less than") {
          value.rule_condition = "<";
        }
      } else if (["Expired", "Manual Upload"].includes(value.rule_operation)) {
        value.rule_condition = "equal";
      } else if (["Web Form", "Suspended"].includes(value.rule_operation)) {
        value.rule_condition = "equals";
      } else if (["Mobile Opt-in"].includes(value.rule_operation)) {
        value.rule_condition = "equal to";
      } else if (["eCommerce"].includes(value.rule_operation)) {
        value.rule_condition = "is equal";
      } else if (
        value.rule_condition == "doesn't contain" ||
        value.rule_condition == "Doesn't Contain"
      ) {
        value.rule_condition = "does not contain";
      } else if (value.rule_condition === "Equals") {
        value.rule_condition = "is";
      } else if (value.rule_condition === "Doesn't equal") {
        value.rule_condition = "is not";
      } else if (value.rule_condition === "One of") {
        value.rule_condition = "is in";
      } else if (value.rule_condition == "Contains") {
        value.rule_condition = "contains";
      } else if (value.rule_condition == "Only like") {
        value.rule_condition = "only like";
      } else if (value.rule_condition == "Issued") {
        value.rule_condition = "is value";
      } else if (value.rule_condition == "Redeemed") {
        value.rule_condition = "is in redeem";
      } else if (value.rule_condition == "Not Redeemed") {
        value.rule_condition = "is not in redeem";
      }


      if (
        (value.rule_condition === "is" && value.rule_operation === "Today")
      ) {
        value.rule_condition = "isToday";
      }
    });
  });
}

/*GET SEGMENT OR DATA*/
function getSegmentOrRule(getOrdata: any) {
  for (const dataObject of getOrdata) {
    if (dataObject) {
      const { or_index, rule_id, values } = dataObject;
      const existingEntryIndex = orSegment.value.findIndex(
        (entry) => entry.or_index === or_index && entry.rule_id === rule_id
      );
      if (existingEntryIndex !== -1) {
        orSegment.value[existingEntryIndex] = { or_index, rule_id, values };
      } else {
        orSegment.value.push({ or_index, rule_id, values });
      }
      selectedOrSegment.value = orSegment.value;
    }
  }
}
/*REMOVE OR RULE FROM SYNTEXT WHEN REMOVE OR RULE */
function removeSegmentOrRule(orIndex, rule_id) {
  const indexToRemoveOr = orSegment.value.findIndex(
    (panel) => panel.or_index === orIndex && panel.rule_id === rule_id
  );
  if (indexToRemoveOr !== -1) {
    orSegment.value.splice(indexToRemoveOr, 1);
  }
}

/*GET SEGMENT AND DATA*/
function getSegmentAndRule(getAnddata) {
  for (const dataObject of getAnddata) {
    if (dataObject) {
      const { and_con_index, and_or_index, rule_id, values } = dataObject;
      const existingEntryIndex = andOrSegment.value.findIndex(
        (entry) =>
          entry.and_con_index === and_con_index &&
          entry.and_or_index === and_or_index &&
          entry.rule_id === rule_id
      );
      if (existingEntryIndex !== -1) {
        andOrSegment.value[existingEntryIndex] = {
          and_con_index,
          and_or_index,
          rule_id,
          values,
        };
      } else {
        andOrSegment.value.push({
          and_con_index,
          and_or_index,
          rule_id,
          values,
        });
      }
      selectedAndSegment.value = andOrSegment.value;
    }
  }
}

/*REMOVE AND RULE FROM SYNTEXT WHEN REMOVE OR FROM AND RULE */
function removeSegmentAndRule(andIndex, andOrIndex, rule_id) {
  const indexToRemoveOrAnd = andOrSegment.value.findIndex(
    (panel) =>
      panel.and_con_index == andIndex &&
      panel.and_or_index == andOrIndex &&
      panel.rule_id == rule_id
  );
  if (indexToRemoveOrAnd != -1) {
    andOrSegment.value.splice(indexToRemoveOrAnd, 1);
  }
}

/*REMOVE RULE TYPE PANEL AND ALSO REMOVE OR / AND Rules*/
function removePanel(id) {
  const indexToRemove = expansionPanels.value.findIndex(
    (panel) => panel.id === id
  );
  if (indexToRemove != -1) {
    expansionPanels.value.splice(indexToRemove, 1);
    activeButtonIndex.value.splice(indexToRemove, 1);
  }
  /*Remove OR RULES*/
  const indicesOrRule = selectedOrSegment.value.map((item, index) =>
    (item.rule_id === id ? index : -1)).filter(index => index !== -1);
  indicesOrRule.reverse();
  indicesOrRule.forEach(index => {
    selectedOrSegment.value.splice(index, 1);
  });
  /*Remove AND RULES*/
  const indicesAndRule = selectedAndSegment.value.map((item, index) =>
    (item.rule_id === id ? index : -1)).filter(index => index !== -1);
  indicesAndRule.reverse();
  indicesAndRule.forEach(index => {
    selectedAndSegment.value.splice(index, 1);
  });
}

function updateSelectedAndSegment(index: number, id: string | number) {
  const itemIndex = selectedAndSegment.value.findIndex(item => item.and_con_index == index && item.rule_id == id);
  // If the item is found, remove it from the array
  if (itemIndex != -1) {
    selectedAndSegment.value.splice(itemIndex, 1);
    console.log('Item removed successfully');
  } else {
    console.log('Item not found');
  }
}

function updateSelectedOrSegment(index: number, id: string | number) {
  console.log(index, id, selectedOrSegment.value)
  const itemIndex = selectedOrSegment.value.findIndex(item => item.or_index == index && item.rule_id == id);
  // If the item is found, remove it from the array
  if (itemIndex != -1) {
    selectedOrSegment.value.splice(itemIndex, 1);
  } else {
    console.log('Item not found');
  }
}

/*CHANGE HEADER ON SCROLL */
function handleScroll() {
  var scrollPosition = window.scrollY;
  isScroll.value = scrollPosition !== 0;
  if (scrollPosition >= 0 && scrollPosition <= window.innerHeight) {
    screenHeight.value = true;
  } else {
    screenHeight.value = false;
  }
}

function subStringSegRule(segRule) {
  const firstPipesIndex = segRule.indexOf("||");
  const secondColonIndex = segRule.indexOf(":", segRule.indexOf(":") + 1);

  if (firstPipesIndex < secondColonIndex) {
    return segRule.substring(firstPipesIndex + 2);
  } else if (firstPipesIndex > secondColonIndex) {
    return segRule.substring(secondColonIndex + 1);
  } else {
    return segRule;
  }
}
/* GET EDIT SEGMENT DETAILS AND UPDATE */
function loadSegment() {
  const route = useRoute();

  // const params = {
  //   segmentID: route.params.id,
  // };
  let getString = "";
  const editedSegment = JSON.parse(localStorage.getItem("EditedSegment" + route.params.id) ?? "null"
  );
  if (editedSegment != null) {
    segmentId.value = route.params.id + "";
    console.log("editedSegment.segRule :: ", editedSegment.segRule);
    console.log("subStringSegRule(editedSegment.segRule) >> ", subStringSegRule(editedSegment.segRule));
    segment_rule = editedSegment.segRule
    subStringSegRule(editedSegment.segRule);
    totSize.value = editedSegment.totSize;
    totEmailSize.value = editedSegment.totEmailSize;
    totMobileSize.value = editedSegment.totMobileSize;
    segmentName.value = editedSegment.segRuleName;
    description.value = editedSegment.description;
    segmentNameTextBoxIsDisabled.value = true;

    getString = subStringSegRule(editedSegment.segRule);
  }
  //const getString = "OptCulture Promotion Name|cp.coupon_id|String:is|ABCD<OR>||Promo-code Date|cd.status|string:is redeemed|Redeemed<OR>||Promo-code Date|date(cd.redeemed_on)|date:isToday|Today<OR>Promo-code Status|cd.status|String:is value|IN('Active','Redeemed')<OR>||Promo-code Date|cd.status|string:is expired|Active','Expired<OR>||Promo-code Date|date(cd.expired_on)|date:iswithinnext_days|4<OR>Promo-code Date|cd.status|string:is expired|Active','Expired<OR>||Promo-code Date|date(cd.expired_on)|date:isequal|2024-02-12<OR>";
  //const getString = ''
  if (getString) {
    const searchString = getString.toLowerCase();
    let conditionsToRemove = [];

    if (searchString.includes("in('active','redeemed')") || searchString.includes('notactive') || searchString.includes('redeemed') || searchString.includes('String:is not in redeem')) {
      conditionsToRemove = [
        "Promo-code Date|cd.status|string:is issued|Active<OR>||",
        "Promo-code Date|cd.status|string:is redeemed|Redeemed<OR>||",
        "Promo-code Date|cd.status|string:is expired|Active','Expired<OR>||"
      ];
    }
    const pattern = new RegExp(conditionsToRemove.map(condition => condition.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('|'), 'g');
    const modifiedString = getString.replace(pattern, '');

    const stringWithAsterisk = modifiedString.replace(/\|\|/g, "||*");

    const splitstringAsterisk = stringWithAsterisk.split("*");

    const formattedList = ruleTypeKey
      .map((item) => {
        return splitstringAsterisk
          .filter((rule) => {
            return item.some((keyword) => rule.includes(keyword));
          })
          .join("");
      })
      .filter(Boolean);
    const gstring = formattedList.map((string, i) => {
      const maRuleIndex = findMatchingRuleIndex(string, rules_type);
      if (maRuleIndex != -1) {
        activeButtonIndex.value.push(maRuleIndex);
        return {
          index: maRuleIndex,
          title: rules_type[maRuleIndex].title,
          id: rules_type[maRuleIndex].id,
          segmentRules: rules_type[maRuleIndex].rules,
          rule_string: string,
        };
      };
    });
    expansionPanels.value = gstring;
  }
}
/*ADD THE RULE PANEL WHEN RULE KEY MATCH FROM STRING while UPDATE*/
function findMatchingRuleIndex(searchString, rules) {
  for (let i = 0; i < rules.length; i++) {
    const rule = rules[i];
    if (rule.rules) {
      for (const element of rule.rules) {
        const subRule = element;
        if (subRule.rule_name_string == 'P.UDF') {
          if (subRule.rule_name_key) {
            if (searchString.includes('sal.udf') || searchString.includes('sku.udf')) {
              return i;
            }
          }
        } else {
          if (subRule.rule_name_key) {
            for (const element of subRule.rule_name_key) {
              const key = element;
              if (key == 'date(cd.issued_on)') {
                if (searchString.includes('Active') || searchString.includes('Redeemed') || searchString.includes('notactive')) {
                  return i;
                }
              }
              else if (key == 'ce.event_type') {
                if (searchString.includes('ce.event_type')) {
                  return i;
                }
              } else {
                if (searchString.includes(key)) {
                  return i;
                }
              }
            }
          }
        }
      }
    }
  }
  return -1;
}
function andOrConditions(value) {
  conditionsAndOR.value = value
}

function orConditions(value) {
  conditionsOR.value = value
}

function validation(value: string) {
  valid.value = value
}

onMounted(() => {
  /*Add scroll event listener when the component is mounted*/
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  /*Remove scroll event listener when the component is unmounted*/
  window.removeEventListener("scroll", handleScroll);
});
loadSegment();
loadUDF()
rules_type[1].rules[0].rule_condition = Object.values(interactionRules)
</script>

<template>
  <section :class="{ screen_height: screenHeight }">
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 580" :model-value="openDialogBox" persistent>
      <!-- Dialog close btn -->
      <DialogCloseBtn @click="openDialogBox = false" />

      <VCard class="pa-2" elevation="6">
        <VCardText class="">
          <div class="text-center text-h6 font-weight-medium">
            <p>{{ dialogBoxTitle }}</p>
          </div>
          <VRow class="mt-2">
            <VCol cols="12" class="text-center">
              <VBtn class="me-3" color="primary" @click="saveSegment()" v-if="yesBtnEnable">
                Yes
              </VBtn>

              <VBtn color="#D7D7C1" @click="closeDialogBox()">
                {{ noBtnText }}
              </VBtn>
            </VCol>
          </VRow>
        </VCardText>
      </VCard>
    </VDialog>

    <v-row>
      <v-col cols="12">
        <v-col cols="12" class="d-flex flex-wrap align-center px-0" :class="{ 'header-display': isScroll }">
          <v-col cols="12" md="4" sm="6">
            <v-card height="126" class="d-flex align-center">
              <div class="pa-10">
                <div class="d-flex">
                  <div class="text-subtitle-2">Contacts in this segment</div>
                  <div class="ml-16">
                    <svg style="
                        background: linear-gradient(
                          124deg,
                          rgb(124, 1, 202) 14.81%,
                          rgb(168, 1, 86) 71.61%,
                          rgb(167 14 21) 40.61%
                        );
                      " class="pa-2 rounded-lg" xmlns="http://www.w3.org/2000/svg" width="40" height="40"
                      viewBox="0 0 24 24" fill="none">
                      <path d="M14 3V7C14 7.55228 14.4477 8 15 8H19" stroke="white" stroke-width="1.5"
                        stroke-linecap="round" stroke-linejoin="round" />
                      <path d="M12 21H7C5.89543 21 5 20.1046 5 19V5C5 3.89543 5.89543 3 7 3H14L19 8V12.5" stroke="white"
                        stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                      <circle cx="16.5" cy="17.5" r="2.5" stroke="white" stroke-width="1.5" stroke-linecap="round"
                        stroke-linejoin="round" />
                      <path d="M18.5 19.5L21 22" stroke="white" stroke-width="1.5" stroke-linecap="round"
                        stroke-linejoin="round" />
                    </svg>
                  </div>
                </div>
                <div class="text-h4">
                  {{ totSize }}
                  <v-progress-circular :size="20" :width="2" color="primary" class="mb-1 ml-2" indeterminate
                    v-if="refresh"></v-progress-circular>
                  <VIcon icon="tabler-refresh" size="20" class="mb-1 ml-2" @click="refreshSegmentCounts()" v-else>
                  </VIcon>
                </div>
              </div>
            </v-card>
          </v-col>
          <v-col cols="6" md="2">
            <v-card height="126" class="text-center d-flex justify-center align-center">
              <div>
                <svg style="background: #ff9f433d" class="pa-2 rounded-circle" xmlns="http://www.w3.org/2000/svg"
                  width="44" height="44" viewBox="0 0 25 24" fill="none">
                  <rect x="3.5" y="5" width="18" height="14" rx="2" stroke="#FF9F43" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M3.5 7L12.5 13L21.5 7" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
                <div class="text-subtitle-2">{{ totEmailSize }}</div>
                <div class="text-caption">email addresses</div>
              </div>
            </v-card>
          </v-col>
          <v-col cols="6" md="2">
            <v-card height="126" class="text-center d-flex justify-center align-center">
              <div>
                <div>
                  <svg style="background: #28c76f29" class="pa-2 rounded-circle" xmlns="http://www.w3.org/2000/svg"
                    width="44" height="44" viewBox="0 0 25 24" fill="none">
                    <rect x="7.5" y="4" width="10" height="16" rx="1" stroke="#28C76F" stroke-width="1.5"
                      stroke-linecap="round" stroke-linejoin="round" />
                    <path d="M11.5 5H13.5" stroke="#28C76F" stroke-width="1.5" stroke-linecap="round"
                      stroke-linejoin="round" />
                    <path d="M12.5 17V17.01" stroke="#28C76F" stroke-width="1.5" stroke-linecap="round"
                      stroke-linejoin="round" />
                  </svg>
                </div>
                <div class="text-subtitle-2">{{ totMobileSize }}</div>
                <div class="text-caption">mobile numbers</div>
              </div>
            </v-card>
          </v-col>
          <v-col cols="12" md="4" sm="6">
            <v-card height="126" class="text-center d-flex justify-center align-center launch-in-active"
              :class="{ 'launch-active': is_segment_save }">
              <div>
                <div>
                  <svg xmlns="http://www.w3.org/2000/svg" width="56" height="56" viewBox="0 0 56 56" fill="none">
                    <path opacity="0.2" fill-rule="evenodd" clip-rule="evenodd"
                      d="M46.5717 32.4625L39.703 24.2156C39.9655 29.2688 38.7186 35.1531 34.8686 41.8688L41.4311 47.1188C41.6645 47.3041 41.9413 47.4267 42.2353 47.4751C42.5293 47.5235 42.8308 47.496 43.1113 47.3952C43.3917 47.2944 43.6417 47.1237 43.8377 46.8992C44.0337 46.6748 44.1691 46.404 44.2311 46.1125L46.9217 33.95C46.9866 33.6922 46.9887 33.4225 46.9278 33.1637C46.8669 32.9048 46.7448 32.6644 46.5717 32.4625ZM9.29671 32.5938L16.1655 24.3688C15.903 29.4219 17.1498 35.3063 20.9998 42L14.4373 47.25C14.2055 47.4353 13.9303 47.5585 13.6376 47.6082C13.345 47.6578 13.0446 47.6322 12.7645 47.5338C12.4845 47.4353 12.2342 47.2673 12.037 47.0455C11.8398 46.8236 11.7023 46.5553 11.6373 46.2656L8.94671 34.0813C8.88185 33.8234 8.87978 33.5537 8.94068 33.2949C9.00158 33.0361 9.12367 32.7956 9.29671 32.5938Z"
                      fill="white" />
                    <path fill-rule="evenodd" clip-rule="evenodd"
                      d="M26.2739 3.55627C26.7592 3.15452 27.3695 2.93457 27.9997 2.93457C28.6315 2.93457 29.2433 3.1556 29.7291 3.55924C31.8522 5.28779 36.4476 9.57581 38.9846 16.1702C39.8647 18.4578 40.492 21.0105 40.6883 23.814L47.4673 31.9489C47.7418 32.271 47.9355 32.6539 48.0324 33.0658C48.1287 33.4751 48.1267 33.9012 48.0267 34.3094L45.3388 46.4812L45.3381 46.4843C45.2361 46.9395 45.02 47.3612 44.7101 47.7098C44.4003 48.0584 44.0068 48.3224 43.5668 48.4771C43.1268 48.6318 42.6547 48.6721 42.1948 48.5941C41.735 48.5161 41.3025 48.3224 40.9381 48.0312L40.9377 48.0308L34.6491 43H21.3506L15.062 48.0308L15.0616 48.0312C14.6972 48.3224 14.2647 48.5161 13.8049 48.5941C13.345 48.6721 12.8729 48.6318 12.4329 48.4771C11.9929 48.3224 11.5994 48.0584 11.2896 47.7098C10.9797 47.3612 10.7636 46.9395 10.6616 46.4844L10.6609 46.4812L7.97301 34.3094C7.87298 33.9012 7.87097 33.4751 7.96726 33.0658C8.06425 32.6537 8.25814 32.2706 8.53281 31.9484L15.1833 23.9847C15.362 21.1101 16.0016 18.4963 16.9094 16.159C19.4714 9.56285 24.1265 5.27547 26.2739 3.55627ZM38.7189 24.3723C38.7045 24.2925 38.6999 24.2114 38.7049 24.1311C38.5369 21.451 37.9435 19.034 37.118 16.8883C34.7567 10.7506 30.4537 6.72754 28.4623 5.10685L28.4524 5.09882L28.4525 5.09876C28.3254 4.99268 28.1652 4.93457 27.9997 4.93457C27.8342 4.93457 27.674 4.99268 27.547 5.09876L27.5307 5.11211C25.5185 6.72184 21.1579 10.7447 18.7737 16.8831C17.9167 19.0895 17.3092 21.5832 17.1654 24.3551C17.166 24.3974 17.1639 24.4398 17.1591 24.482C16.939 29.2015 18.0641 34.7223 21.5815 41H34.4122C37.8851 34.6731 38.9695 29.1157 38.7189 24.3723ZM45.9348 33.2339L40.7225 26.9791C40.5135 31.3826 39.2246 36.3204 36.2834 41.7462L42.1866 46.4687C42.2859 46.5481 42.4039 46.6009 42.5293 46.6222C42.6547 46.6435 42.7835 46.6325 42.9035 46.5903C43.0235 46.5481 43.1308 46.4761 43.2153 46.3811C43.2996 46.2863 43.3584 46.1717 43.3863 46.048L43.3866 46.0468L46.0765 33.8656L46.0791 33.8541L46.0832 33.8373C46.109 33.7345 46.1099 33.6271 46.0856 33.5239C46.0613 33.4208 46.0127 33.325 45.9437 33.2445L45.9347 33.234L45.9348 33.2339ZM15.1743 27.1157L10.0643 33.2347L10.056 33.2446L10.056 33.2445C9.98702 33.325 9.93836 33.4208 9.91409 33.5239C9.88983 33.6271 9.89065 33.7345 9.9165 33.8373L9.9188 33.8466L9.92319 33.8656L12.6131 46.0469L12.6134 46.0481C12.6413 46.1717 12.7002 46.2863 12.7844 46.3811C12.8689 46.4761 12.9762 46.5481 13.0962 46.5903C13.2162 46.6325 13.345 46.6435 13.4704 46.6222C13.5958 46.601 13.7137 46.5481 13.8131 46.4687L19.7105 41.7509C16.7409 36.3814 15.4167 31.4875 15.1743 27.1157ZM23.4999 49C23.4999 48.4477 23.9476 48 24.4999 48H31.4999C32.0521 48 32.4999 48.4477 32.4999 49C32.4999 49.5523 32.0521 50 31.4999 50H24.4999C23.9476 50 23.4999 49.5523 23.4999 49ZM30.6249 21C30.6249 22.4497 29.4496 23.625 27.9999 23.625C26.5501 23.625 25.3749 22.4497 25.3749 21C25.3749 19.5502 26.5501 18.375 27.9999 18.375C29.4496 18.375 30.6249 19.5502 30.6249 21Z"
                      fill="white" />
                  </svg>
                </div>
                <div :class="[is_segment_save ? 'text-white' : 'text-caption']">
                  Launch a Campaign
                </div>
              </div>
            </v-card>
          </v-col>
        </v-col>
        <!-- SHOW WHEN SCROLL DOWN START-->
        <div class="sticky-rules mb-3" :class="{ 'header-display': !isScroll }">
          <v-col cols="12" class="d-flex flex-wrap align-center px-0 mb-3">
            <v-col cols="12" md="2" class="pa-2">
              <v-card class="pa-1">
                <div class="text-h6 text-center">{{ segmentName == '' ? 'Segment Name' : segmentName }}</div>
              </v-card>
            </v-col>
            <v-col cols="6" md="2" class="pa-2">
              <v-card class="pa-1 px-3 d-flex align-center">
                <svg class="mr-2" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                  fill="none">
                  <path d="M14 3V7C14 7.55228 14.4477 8 15 8H19" stroke="#7367f0" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M12 21H7C5.89543 21 5 20.1046 5 19V5C5 3.89543 5.89543 3 7 3H14L19 8V12.5" stroke="#7367f0"
                    stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                  <circle cx="16.5" cy="17.5" r="2.5" stroke="#7367f0" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                  <path d="M18.5 19.5L21 22" stroke="#7367f0" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
                <div class="text-h6 align-end">{{ totSize }}</div>
              </v-card>
            </v-col>
            <v-col cols="6" md="2" class="pa-2">
              <div>
                <!--
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                >
                  <path
                    d="M19.2571 11.1033C19.3142 11.5136 19.693 11.8 20.1032 11.7429C20.5135 11.6859 20.7999 11.3071 20.7429 10.8968L19.2571 11.1033ZM3.80768 8.71166C3.64838 9.09402 3.82922 9.53311 4.21158 9.6924C4.59394 9.85169 5.03303 9.67086 5.19232 9.2885L3.80768 8.71166ZM4.75 5.00012C4.75 4.58591 4.41421 4.25012 4 4.25012C3.58579 4.25012 3.25 4.58591 3.25 5.00012H4.75ZM4 9.00012H3.25C3.25 9.41434 3.58579 9.75012 4 9.75012V9.00012ZM8 9.75012C8.41421 9.75012 8.75 9.41434 8.75 9.00012C8.75 8.58591 8.41421 8.25012 8 8.25012V9.75012ZM20.7429 10.8968C20.1936 6.94443 17.0672 3.84845 13.1096 3.3378L12.9177 4.82547C16.2045 5.24957 18.801 7.8208 19.2571 11.1033L20.7429 10.8968ZM13.1096 3.3378C9.15206 2.82714 5.34223 5.02813 3.80768 8.71166L5.19232 9.2885C6.46679 6.2293 9.63088 4.40136 12.9177 4.82547L13.1096 3.3378ZM3.25 5.00012V9.00012H4.75V5.00012H3.25ZM4 9.75012H8V8.25012H4V9.75012Z"
                    fill="#4B465C"
                  />
                  <path
                    d="M19.2571 11.1033C19.3142 11.5136 19.693 11.8 20.1032 11.7429C20.5135 11.6859 20.7999 11.3071 20.7429 10.8968L19.2571 11.1033ZM3.80768 8.71166C3.64838 9.09402 3.82922 9.53311 4.21158 9.6924C4.59394 9.85169 5.03303 9.67086 5.19232 9.2885L3.80768 8.71166ZM4.75 5.00012C4.75 4.58591 4.41421 4.25012 4 4.25012C3.58579 4.25012 3.25 4.58591 3.25 5.00012H4.75ZM4 9.00012H3.25C3.25 9.41434 3.58579 9.75012 4 9.75012V9.00012ZM8 9.75012C8.41421 9.75012 8.75 9.41434 8.75 9.00012C8.75 8.58591 8.41421 8.25012 8 8.25012V9.75012ZM20.7429 10.8968C20.1936 6.94443 17.0672 3.84845 13.1096 3.3378L12.9177 4.82547C16.2045 5.24957 18.801 7.8208 19.2571 11.1033L20.7429 10.8968ZM13.1096 3.3378C9.15206 2.82714 5.34223 5.02813 3.80768 8.71166L5.19232 9.2885C6.46679 6.2293 9.63088 4.40136 12.9177 4.82547L13.1096 3.3378ZM3.25 5.00012V9.00012H4.75V5.00012H3.25ZM4 9.75012H8V8.25012H4V9.75012Z"
                    fill="white"
                    fill-opacity="0.2"
                  />
                  <path
                    d="M4.74286 12.8968C4.68584 12.4865 4.30703 12.2001 3.89676 12.2571C3.48649 12.3142 3.20012 12.693 3.25714 13.1032L4.74286 12.8968ZM20.1923 15.2884C20.3516 14.9061 20.1708 14.467 19.7884 14.3077C19.4061 14.1484 18.967 14.3292 18.8077 14.7116L20.1923 15.2884ZM19.25 19C19.25 19.4142 19.5858 19.75 20 19.75C20.4142 19.75 20.75 19.4142 20.75 19H19.25ZM20 15H20.75C20.75 14.5858 20.4142 14.25 20 14.25V15ZM16 14.25C15.5858 14.25 15.25 14.5858 15.25 15C15.25 15.4142 15.5858 15.75 16 15.75V14.25ZM3.25714 13.1032C3.80641 17.0556 6.93276 20.1516 10.8904 20.6623L11.0823 19.1746C7.7955 18.7505 5.19904 16.1793 4.74286 12.8968L3.25714 13.1032ZM10.8904 20.6623C14.8479 21.1729 18.6578 18.972 20.1923 15.2884L18.8077 14.7116C17.5332 17.7708 14.3691 19.5987 11.0823 19.1746L10.8904 20.6623ZM20.75 19V15H19.25V19H20.75ZM20 14.25H16V15.75H20V14.25Z"
                    fill="#4B465C"
                  />
                  <path
                    d="M4.74286 12.8968C4.68584 12.4865 4.30703 12.2001 3.89676 12.2571C3.48649 12.3142 3.20012 12.693 3.25714 13.1032L4.74286 12.8968ZM20.1923 15.2884C20.3516 14.9061 20.1708 14.467 19.7884 14.3077C19.4061 14.1484 18.967 14.3292 18.8077 14.7116L20.1923 15.2884ZM19.25 19C19.25 19.4142 19.5858 19.75 20 19.75C20.4142 19.75 20.75 19.4142 20.75 19H19.25ZM20 15H20.75C20.75 14.5858 20.4142 14.25 20 14.25V15ZM16 14.25C15.5858 14.25 15.25 14.5858 15.25 15C15.25 15.4142 15.5858 15.75 16 15.75V14.25ZM3.25714 13.1032C3.80641 17.0556 6.93276 20.1516 10.8904 20.6623L11.0823 19.1746C7.7955 18.7505 5.19904 16.1793 4.74286 12.8968L3.25714 13.1032ZM10.8904 20.6623C14.8479 21.1729 18.6578 18.972 20.1923 15.2884L18.8077 14.7116C17.5332 17.7708 14.3691 19.5987 11.0823 19.1746L10.8904 20.6623ZM20.75 19V15H19.25V19H20.75ZM20 14.25H16V15.75H20V14.25Z"
                    fill="white"
                    fill-opacity="0.2"
                  />
                </svg>
		-->
                <v-progress-circular :size="20" :width="2" color="primary" class="mb-1" indeterminate
                  v-if="refresh"></v-progress-circular>
                <VIcon icon="tabler-refresh" size="20" class="mb-1" @click="refreshSegmentCounts()" v-else></VIcon>
              </div>
            </v-col>
            <v-col cols="6" md="2" class="pa-2">
              <v-card class="pa-1 px-3 d-flex align-center" style="background-color: #ff9f433d">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 25 24" fill="none"
                  class="mr-2">
                  <rect x="3.5" y="5" width="18" height="14" rx="2" stroke="#FF9F43" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M3.5 7L12.5 13L21.5 7" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
                <div class="text-h6 align-end">{{ totEmailSize }}</div>
              </v-card>
            </v-col>
            <v-col cols="6" md="2" class="pa-2">
              <v-card class="pa-1 px-3 d-flex align-center" style="background-color: #28c76f29">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 25 24" fill="none"
                  class="mr-2">
                  <rect x="7.5" y="4" width="10" height="16" rx="1" stroke="#28C76F" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M11.5 5H13.5" stroke="#28C76F" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                  <path d="M12.5 17V17.01" stroke="#28C76F" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
                <div class="text-h6 align-end">{{ totMobileSize }}</div>
              </v-card>
            </v-col>
            <v-col cols="12" md="2" class="pa-2">
              <v-btn style="
                  background: linear-gradient(
                    272deg,
                    #7b00ca 10.85%,
                    #c50009 90.34%
                  );
                  width: 100%;
                " @click="validateSegmentBeforeSaving()">
                <div class="text-white">Save Segment</div>
              </v-btn>
            </v-col>
          </v-col>
          <div :class="{ 'header-display': !isScroll }">
            <v-row class="d-flex justify-center segment_media">
              <v-btn v-for="(rule, index) in rules_type" :key="index" rounded="lg" color="indigo-darken-1"
                variant="outlined" @click="addRule(index)" :class="{ 'rule-active': activeButtonIndex.includes(index) }"
                class="mr-4 mb-2">
                <div class="rule-text-white">{{ rule.title }}</div>
              </v-btn>
            </v-row>
          </div>
        </div>
        <!-- SHOW WHEN SCROLL DOWN DOWN-->
        <v-card class="my-5" :class="{ 'header-display': isScroll }">
          <v-col cols="12" class="d-flex">
            <v-row>
              <v-col cols="8">
                <div>Name of the Segment</div>
                <AppTextField placeholder="Untitled_Segment" v-model="segmentName"
                  :disabled="segmentNameTextBoxIsDisabled" />
              </v-col>
              <v-col cols="4" class="d-flex justify-end align-end">
                <v-btn style="
                    background: linear-gradient(
                      272deg,
                      #7b00ca 10.85%,
                      #c50009 90.34%
                    );
                  " @click="validateSegmentBeforeSaving()">
                  <div class="text-white">Save Segment</div>
                </v-btn>
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="12" class="d-flex">
            <AppTextField placeholder="Purpose" v-model="description" />
          </v-col>
        </v-card>
        <v-card class="my-5 rules-default">
          <v-col cols="12" class="ml-0">
            <div :class="{ 'header-display': isScroll }" class="d-flex my-5">
              <v-icon icon="tabler-ruler" size="30" color="#7367F0"></v-icon>
              <div style="color: #7367f0" class="text-h6 ml-3">
                Choose a Rule type
              </div>
            </div>
            <v-sheet :class="{ 'header-display': isScroll }" class="d-flex flex-wrap gap-2">
              <v-btn v-for="(rule, index) in rules_type" :key="index" size="large" rounded="lg" color="indigo-darken-1"
                variant="outlined" @click="addRule(index)"
                :class="{ 'rule-active': activeButtonIndex.includes(index) }">
                <div class="rule-text-white">{{ rule.title }}</div>
              </v-btn>
            </v-sheet>
          </v-col>
          <v-col cols="12">
            <div class="d-flex justify-center mt-10" v-if="expansionPanels.length == 0">
              <v-card class="text-center" width="550" height="200">
                <svg class="pa-2 rounded-circle" xmlns="http://www.w3.org/2000/svg" style="background: #ff9f4366"
                  width="42" height="42" viewBox="0 0 25 24" fill="none">
                  <path fill-rule="evenodd" clip-rule="evenodd" d="M17.5 3L21.5 7L7.5 21L3.5 17L17.5 3Z"
                    stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                  <path d="M16.5 7L15 5.5" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                  <path d="M13.5 10L12 8.5" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                  <path d="M10.5 13L9 11.5" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                  <path d="M7.5 16L6 14.5" stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
                <v-card-title class="text-h6">Add rules to create customized segments</v-card-title>
                <v-card-text class="text-caption">If you want to target a specific type of audience you want to
                  create set rules and filter your entire database to create a
                  custom audience.</v-card-text>
              </v-card>
            </div>
            <!--  New RULES Add Start -->
            <div class="rules-add">
              <Rules v-for="(item, index) in expansionPanels" :key="item.id" :title="item.title"
                :showAndText="index < expansionPanels.length - 1" :reference="item.id" :segmentRules="item.segmentRules"
                :genratedString="item.rule_string" @getSelectedOrRules="getSegmentOrRule"
                @getSelectedAndRules="getSegmentAndRule" @removeSelectedOrRules="removeSegmentOrRule"
                @removeSelectedAndRules="removeSegmentAndRule" @remove-panel="removePanel"
                @updatedSelectedAndSegment="updateSelectedAndSegment"
                @updatedSelectedOrSegment="updateSelectedOrSegment" @validation="validation"
                :code="editedDiscountedCode" :couponCodes="Object.keys(coupon_codes)"
                :campaignNames="Object.values(interactionRules)" :tier="editedTier" @andORConditions="andOrConditions"
                @orLength="orConditions" :loyaltyTiers="loyaltyTiers" :udfs="Object.keys(UDF)" :selectedUdf="editedUDF"
                :editedPurchaseUDF="editedPurchaseUDF" :purchaseudfs="Object.keys(purchaseUDF)" />
            </div>
            <!--  New RULES Add END -->
          </v-col>
        </v-card>
      </v-col>
    </v-row>

  </section>
</template>

<style scoped>
.rule-active {
  background-color: #7367f0;
}

.rule-active .rule-text-white {
  color: white;
}

.rule-panel-active {
  opacity: 1 !important;
}

.launch-in-active {
  background: #dbdade;
}

.launch-active {
  background: linear-gradient(131deg, #7c01ca -12.43%, #c50109 136.29%);
}

.layout-wrapper.window-scrolled .sticky-rules {
  position: sticky;
  top: 80px;
  z-index: 9;
  background: #eeeef1;
  left: 260px;
  right: 0;
}

.layout-wrapper.window-scrolled .rules-default {
  padding-bottom: 125px;
}

.rules-add::-webkit-scrollbar {
  width: 10px;
  display: none;
}

.rules-add::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.rules-add::-webkit-scrollbar-thumb {
  background: #888;
}

.header-display {
  display: none !important;
  opacity: 0;
  transform: translateY(-100%);
  transition: opacity 0.8s ease-in-out, transform 0.8s ease-in-out;
}

.screen_height {
  height: 100vh;
}

@media screen and (max-width: 1260px) {
  .segment_media {
    overflow-x: auto;
    flex-wrap: nowrap;
    justify-content: start !important;
  }
}
</style>
