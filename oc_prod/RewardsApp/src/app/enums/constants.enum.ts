let shoppers = {
    brand : 'Shoppers',
    hero_image : 'assets/images/shoppers.png',
    optAccountName: 'Shoppers',
    optAccountPassword: 'RYXWVLD8UCG6GADZ',
    orgId: 'Shoppers',
    token: 'm2e2jo',
    showRewardsPage: false
};

let wakefield = {
    brand : 'Wakefield\'s',
    hero_image : 'assets/images/wakefeild.png',
    optAccountName: 'WakefieldsAPP',
    optAccountPassword: 'RYXWVLD8UCG6GADZ',
    orgId: 'WakefieldsAPP',
    token: 's2tyxg',
    showRewardsPage: false
};

let martins = {
    brand : 'Martin',
    hero_image : 'assets/images/wakefeild.png',
    optAccountName: 'WakefieldsAPP',
    optAccountPassword: 'RYXWVLD8UCG6GADZ',
    orgId: 'WakefieldsAPP',
    token: 's2tyxg',
    showRewardsPage: true
};

let martinsProd = {
    brand : 'Martin',
    hero_image : 'assets/images/martins.jpg',
    optAccountName: 'Wakefields',
    optAccountPassword: 'RYXWVLD8UCG6GADZ',
    orgId: 'Wakefields',
    token: 'WKFS0JUT2UDXCYNK',
    showRewardsPage: true
}

let devApp = {
    brand : 'Earthbound Trade',
    hero_image : 'assets/images/earthbound.png',
    optAccountName: 'MobileApp',
    optAccountPassword: '$2a$10$0APc62LqB1VjecldM8ZFJuXZ7JVPvbOG9UrntNFqG/ucA7mHpMXO.',
    orgId: 'MobileApp',
    token: 'gflnmg',
    showRewardsPage: false
}

let earthbound_qc = {
    brand : 'Earthbound Trade',
    hero_image : 'assets/images/earthbound.png',
    optAccountName: 'EBTest',
    optAccountPassword: '$2a$10$0APc62LqB1VjecldM8ZFJuXZ7JVPvbOG9UrntNFqG/ucA7mHpMXO.',
    orgId: 'EBTest',
    token: 'c4cx3a',
    showRewardsPage: false
}


let earthbound_app = {
    brand : 'Earthbound Trade',
    hero_image : 'assets/images/earthbound.png',
    optAccountName: 'EBTest',
    optAccountPassword: '$2a$10$0APc62LqB1VjecldM8ZFJuXZ7JVPvbOG9UrntNFqG/ucA7mHpMXO.',
    orgId: 'EBTrade',
    token: 'DMCM2VFFC7I210JN',
    showRewardsPage: false
}

let active = martins;
export let Constants = {
    REGISTRATATIO_MSG : 'Registration is done sucessfully',
    API_ERROR_MSG : 'Sorry, there seems to be a problem! Please get back to us later.',
    UPDATE_MSG : 'Successfully updated information!',
    optAccountName : active.optAccountName,
    optAccountPassword: active.optAccountPassword,
    orgId: active.orgId,
    token: active.token,
    brand: active.brand,
    hero_image: active.hero_image,
    showRewardsPage: active.showRewardsPage
};

export const SOURCE_TYPE = 'LoyaltyApp'