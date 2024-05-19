<script setup lang="ts">
import Message from "./Message.vue";
import backgroundImg from '@/assets/images/wabackground.jpeg'
interface Props {
    data: {
        header: string,
        msgContent: string,
        footer: string,
        msgType: string
        companyLogo: string,
        companyName: string
    }
}
const props = defineProps<Props>()
// watch(props.data.msgContent, () => {
//     console.log(props.data.msgContent);
// })
// console.log(props.data)
const videourl = computed(() => {
    return props.data.header?.replace('youtu.be', 'youtube.com/embed');
})

const message = computed(() => {
    const msg = ref('')
    // console.log(props.data.header)
    // if(props.data.header){
    if (props.data.msgType == 'IMAGE' )
        msg.value = '<div class="text-center" style="background-size: contain"><img style="max-width:100%; height:120px" src="' + props.data.header + '" alt="image" ></div>'
    else if (props.data.msgType == 'TEXT')
        msg.value = '<p>' + props.data.header + '</p>'
    // else  for video
    else if(props.data.msgType == 'VIDEO')
        msg.value = '<div class="text-center"><video style="max-width:100%; height:120px" controls ><source src="' + props.data.header + '"></video></div>'
    
    // else if(props.data.msgType == 'DOCUMENT')
    //     msg.value = '<div class="text-center"><a href="' + props.data.header + '">'+props.data.header+'</a></div>'
    // }
    msg.value = msg.value.concat(props.data.msgContent);
    msg.value = msg.value.concat('<p style="margin-top:5px">' + props.data.footer + '</p>')
    // console.log(msg.value)
    return msg.value;
})

const msgInfo = computed(() => {
    return {
        message: message.value,
        header: props.data?.header,
        footer: props.data?.footer,
        msgType: props.data?.msgType
    }
    // console.log(msgInfo);
})
</script>
<template>
    <div class="smartphone"> 
         <div class="content">
                <div id="wp-widget">
                    <div id="whatsapp-chat" class="">
                        <div class="whatsapp-chat-header">
                            <div class="whatsapp-chat-avatar">
                                <img v-if="props.data?.companyLogo" :src="props.data?.companyLogo" />
                                <div v-else class="companyLogo"></div>
                            </div>
                            <div class=" text-center">
                                <p class=" text-h6 text-white pt-3">
                                    {{ props.data?.companyName }}
                                </p>
                            </div>
                        </div>

                        <div class="start-chat">
                            <div 
                                class="WhatsappChat__Component-sc-Yvjha whatsapp-chat-body">
                                <Message v-if="msgInfo.message" v-model:msgInfo="msgInfo" />
                            </div>
                        </div>
                    </div>
                </div>
            
        </div>
    </div>
</template>
<style scoped>
@charset "UTF-8";

.overlay {
  height: 0%;
  width: 100%;
  position: inherit;
  z-index: 1;
  top: 0;
  left: 0;
  background-color: rgb(0,0,0);
  background-color: rgba(0,0,0, 0.3);
  overflow-y: hidden;
  transition: 0.5s;
}

.overlay-content {
  position: inherit;
  top: 25%;
  width: 100%;
  text-align: center;
  margin-top: 30px;
}

.overlay a {
  padding: 8px;
  text-decoration: none;
  font-size: 36px;
  color: #818181;
  display: block;
  transition: 0.3s;
}

.overlay a:hover, .overlay a:focus {
  color: #f1f1f1;
}

.overlay .closebtn {
  position: absolute;
  top: 20px;
  right: 45px;
  font-size: 60px;
}

@media screen and (max-height: 450px) {
  .overlay { overflow: auto;
  -ms-overflow-style: none;}
  .overlay a {font-size: 20px}
  .overlay .closebtn {
  font-size: 40px;
  top: 15px;
  right: 35px;
  }
}

.splitLeftLogo { 
	float: right;
	height: 100%;
}
.splitRightLogo { 
  	float: left;
  	height: 100%;
}

.splitLeft { 
	position: relative;
	width: 50%;
	float: left;
	height: 100%;
}
.splitRight { 
	position: relative;
  	width: 50%;
  	float: left;
  	height: 100%;
}
.columnAllign{
    width: 468px;
    float: right;
}
.blockAllign{
	display: flow-root !important;
	margin-left: 32px;
}
.additionalFiles{
    padding-left: 50px;
    margin-bottom: 18px;
}
.zkDesignClass{
    border: 1px solid #CCCCCC;
    border-radius: 5px 5px 5px 5px;
    padding: 0 2px 0 7px;
    text-align: left;
    font-size: 14px
}
.zkHeaderHeight{
	 height: 50px;
}
.zkBodyHeight{
 	 height: 70px;
}
.previewDiv{
	width: 500px;
    margin: auto;
    padding: 32px;
    text-align: justify;
}
/* The device with borders */
.smartphone {
  position: relative;
  width: 100%;
  height: 100%;
  margin: auto;
  border: 16px black solid;
  border-top-width: 60px;
  border-bottom-width: 60px;
  border-radius: 36px;
  background-color: #E1D9D2;
  overflow-y: auto;
  /* padding-top: 20px; */
/* background-image: url("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.peakpx.com%2Fen%2Fhd-wallpaper-desktop-kkldz&psig=AOvVaw2x4h5qvTYKJOCkW-QYtfxX&ust=1710843714766000&source=images&cd=vfe&opi=89978449&ved=0CBMQjRxqFwoTCLim69_L_YQDFQAAAAAdAAAAABAE"); */
background-position: 50% 50%;
z-index: 4;
}

/* The horizontal line on the top of the device */
.smartphone:before {
  content: '';
  display: block;
  width: 60px;
  height: 5px;
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #333;
  border-radius: 10px;
}

/* The circle on the bottom of the device */
/* .smartphone:after {
  content: '';
  display: block;
  width: 35px;
  height: 35px;
  position: absolute;
  left: 50%;
  bottom: -65px;
  transform: translate(-50%, -50%);
  background: #333;
  border-radius: 50%;
} */

/* The screen (or content) of the device */
.smartphone .content {
  /* width: 291px; */
  /* height: 441px; */

  background-repeat: no-repeat;
  background-size: contain;
  word-break: break-all;
  overflow: auto;
  -ms-overflow-style: none;
}
.smartphone .content::-webkit-scrollbar {
  display: none;
}
.block-ellipsis {
  display: -webkit-box;
  max-width: 100%;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 7px;
  /* font-size: 12px; */
  width: 210px;
  font-family: sans-serif !important;
}
.imgStyleMobile{
	border: 0px;
    height: 20px;
    float: right;
    margin: 3px 9px 0px 0px;
}
.imgStyleMobileLeft{
	border: 0px;
    height: 20px;
    float: left;
    margin: 3px 9px 0px 0px;
}
.img-circular{
 width: 20px;
 height: 20px;
 background-image: url('/subscriber/img/favicon.ico');
 background-size: cover;
 display: block;
 margin-left: 6px;
}

.notificationHeader{
	font-size: 70%;
    font-weight: bold;
    max-width: 80%;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
    display: -webkit-box;
    text-overflow: ellipsis;
    font-family: sans-serif !important;
}

.rectangle {
	height: 27px;
    /* width: 290px; */
    background-color: white;
    margin-top: 9px;
    border-radius: 7px;
}
.rectangleNotification {
	height: 210px;
    width: 290px;
    background-color: white;
    margin-top: 5px;
    border-radius: 7px;
}




.close {
    width: 30px;
    height: 30px;
    position: relative;
    margin-left: 39%;
    margin-top: 28%;
    cursor: pointer;
}
.close:after {
    content: '';
    height: 30px;
    border-left: 2px solid #fff;
    position: absolute;
    transform: rotate(45deg);
    left: 28px;
}

.close:before {
    content: '';
    height: 30px;
    border-left: 2px solid #fff;
    position: absolute;
    transform: rotate(-45deg);
    left: 28px;
}


button {
    outline: none;
}

#wp-widget {
    font-family: --system-ui, "Lato", sans-serif;
    /* background: #f1f1f1; */
}

a:link,
/* a:visited {
    color: #444;
    text-decoration: none;
    transition: all 0.4s ease-in-out;
} */

h1 {
    font-size: 20px;
    text-align: center;
    display: block;
    background: linear-gradient(to right top, #6f96f3, #164ed2);
    padding: 20px;
    color: #fff;
    border-radius: 50px;
}

/* CSS Multiple Whatsapp Chat */
#whatsapp-chat {
    box-sizing: border-box !important;
    outline: none !important;
    /* position: fixed; */
    /* width: 100%; */
    
    /* box-shadow: 0 1px 15px rgba(32, 33, 36, 0.28); */

    bottom: 90px;
    right: 30px;
    overflow: hidden;
    /* z-index: 99; */
    /* animation-name: showchat; */
    /* animation-duration: 1s; */
    /* transform: scale(1); */
}

button.blantershow-chat {
    /* background: #009688;
	 */
    background: #fff;
    color: #404040;
    position: fixed;
    display: flex;
    font-weight: 400;
    justify-content: space-between;
    /* z-index: 98; */
    bottom: 25px;
    right: 30px;
    font-size: 15px;
    padding: 15px 15px;
    border-radius: 50%;
    border: 1px solid transparent;
    /* box-shadow: 0 10px 10px rgba(32, 33, 36, 0.28); */
    cursor: pointer;
}

button.blantershow-chat svg {
    transform: scale(1.2);
}

.whatsapp-chat-header {
    background: #095e54;
    display: inline-flex;
    align-items: center;
    padding-left: 10px;
    /* padding-top: 30px; */
    height: 60px;
    width: 100%;
    height: 100%;
    color: #fff;
}

.companyLogo {
    border-radius: 100%;
    width: 50px;
    height: 50px;
    float: left;
    margin: 0 10px 0 0;
    border: 1px solid rgba(0, 0, 0, 0.2);
}

.whatsapp-chat-avatar {
    position: relative;
}

.whatsapp-chat-avatar::after {
    content: "";
    bottom: 0px;
    right: 0px;
    width: 12px;
    height: 12px;
    box-sizing: border-box;
    background-color: #4ad504;
    display: block;
    position: relative;
    /* z-index: 1; */
    border-radius: 50%;
    border: 2px solid #095e54;
    left: 40px;
    top: 38px;
}

.whatsapp-chat-avatar img {
    border-radius: 100%;
    width: 50px;
    float: left;
    margin: 0 10px 0 0;
}

.whatsapp-chat-name-block {
    text-align: left;
}

.info-chat span {
    display: block;
}

#get-label,
span.chat-label {
    font-size: 12px;
    color: #888;
}

#get-nama,
span.chat-nama {
    margin: 5px 0 0;
    font-size: 15px;
    font-weight: 700;
    color: #222;
}

#get-label,
#get-nama {
    color: #fff;
}

span.my-number {
    display: block;
}

/* .blanter-msg {
	 color: #444;
	 padding: 20px;
	 font-size: 12.5px;
	 text-align: center;
	 border-top: 1px solid #ddd;
}
 */
textarea#chat-input {
    border: none;
    font-family: "Arial", sans-serif;
    width: 100%;
    outline: none;
    resize: none;
    padding: 8px;
    font-size: 14px;
}

button#send-it {
    width: 30px;
    font-weight: 700;
    border-color: transparent;
    cursor: pointer;
    background: #eee;
}

button#send-it svg {
    fill: #a6a6a6;
    height: 20px;
    width: 20px;
}

.start-chat .blanter-msg {
    display: flex;
    height: 35px;
}

a.close-chat {
    position: absolute;
    top: 5px;
    right: 15px;
    color: #fff;
    font-size: 30px;
    cursor: pointer;
}

@keyframes ZpjSY {
    0% {
        background-color: #b6b5ba;
    }

    15% {
        background-color: #111;
    }

    25% {
        background-color: #b6b5ba;
    }
}

@keyframes hPhMsj {
    15% {
        background-color: #b6b5ba;
    }

    25% {
        background-color: #111;
    }

    35% {
        background-color: #b6b5ba;
    }
}

@keyframes iUMejp {
    25% {
        background-color: #b6b5ba;
    }

    35% {
        background-color: #111;
    }

    45% {
        background-color: #b6b5ba;
    }
}

@keyframes showhidden {
    from {
        transform: scale(0.5);
        opacity: 0;
    }
}

@keyframes showchat {
    from {
        transform: scale(0);
        opacity: 0;
    }
}

@media screen and (max-width: 480px) {
    #whatsapp-chat {
        width: auto;
        left: 5%;
        right: 5%;
        font-size: 80%;
    }
}

.hidden {
    display: block;
    animation-duration: 0.5s;
    transform: scale(1);
    opacity: 1;
}

.show {
    display: block;
    animation-name: showhidden;
    animation-duration: 0.5s;
    transform: scale(1);
    opacity: 1;
}

.whatsapp-chat-body {
    padding: 20px 20px 20px 10px;
    
    position: relative;
}

.whatsapp-chat-body::before {
    display: block;
    position: absolute;
    content: "";
    left: 0px;
    top: 0px;
    height: 100%;
    width: 100%;
    z-index: 4;
    
}

.quaMo {
    /* z-index: 1; */
}

.vzZsj {
    background-color: #fff;
    width: 52.5px;
    border-radius: 16px;
    display: flex;
    -moz-box-pack: center;
    justify-content: center;
    -moz-box-align: center;
    align-items: center;
    margin-left: 10px;
    opacity: 0;
    transition: all 0.1s ease 0s;
    /* z-index: 1; */
    /* box-shadow: rgba(0, 0, 0, 0.13) 0px 1px 0.5px; */
}

.Yvjha {
    position: relative;
    display: flex;
}

.ixsrax {
    height: 5px;
    width: 5px;
    margin: 0px 2px;
    border-radius: 50%;
    display: inline-block;
    position: relative;
    animation-duration: 1.2s;
    animation-iteration-count: infinite;
    animation-timing-function: linear;
    top: 0px;
    background-color: #9e9da2;
    animation-name: ZpjSY;
}

.dRvxoz {
    height: 5px;
    width: 5px;
    margin: 0px 2px;
    background-color: #b6b5ba;
    border-radius: 50%;
    display: inline-block;
    position: relative;
    animation-duration: 1.2s;
    animation-iteration-count: infinite;
    animation-timing-function: linear;
    top: 0px;
    animation-name: hPhMsj;
}

.kAZgZq {
    padding: 7px 14px 6px;
    background-color: #fff;
    border-radius: 0px 12px 12px;
    position: relative;
    transition: all 0.3s ease 0s;
    opacity: 0;
    transform-origin: center top 0px;
    /* z-index: 2; */
    /* box-shadow: rgba(0, 0, 0, 0.13) 0px 1px 0.5px; */
    margin-top: 20px;
    max-width: calc(100% - 66px);
}

.kAZgZq::before {
    position: absolute;
    background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAmCAMAAADp2asXAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAACQUExURUxpccPDw9ra2m9vbwAAAAAAADExMf///wAAABoaGk9PT7q6uqurqwsLCycnJz4+PtDQ0JycnIyMjPf3915eXvz8/E9PT/39/RMTE4CAgAAAAJqamv////////r6+u/v7yUlJeXl5f///5ycnOXl5XNzc/Hx8f///xUVFf///+zs7P///+bm5gAAAM7Ozv///2fVensAAAAvdFJOUwCow1cBCCnqAhNAnY0WIDW2f2/hSeo99g1lBYT87vDXG8/6d8oL4sgM5szrkgl660OiZwAAAHRJREFUKM/ty7cSggAABNFVUQFzwizmjPz/39k4YuFWtm55bw7eHR6ny63+alnswT3/rIDzUSC7CrAziPYCJCsB+gbVkgDtVIDh+DsE9OTBpCtAbSBAZSEQNgWIygJ0RgJMDWYNAdYbAeKtAHODlkHIv997AkLqIVOXVU84AAAAAElFTkSuQmCC");
    background-position: 50% 50%;
    background-repeat: no-repeat;
    background-size: contain;
    content: "";
    top: 0px;
    left: -12px;
    width: 12px;
    height: 19px;
}

.bMIBDo {
    font-size: 13px;
    font-weight: 700;
    line-height: 18px;
    text-align: left;
    color: rgba(0, 0, 0, 0.4);
}

.iSpIQi {
    font-size: 14px;
    line-height: 19px;
    margin-top: 4px;
    color: #111;
    text-align: left;
}

.cqCDVm {
    text-align: right;
    margin-top: 4px;
    font-size: 12px;
    line-height: 16px;
    color: rgba(17, 17, 17, 0.5);
    margin-right: -8px;
    margin-bottom: -4px;
}</style>
