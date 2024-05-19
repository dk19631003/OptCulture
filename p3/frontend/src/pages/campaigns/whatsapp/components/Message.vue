<template>
    <div class="WhatsappChat__MessageContainer-quaMo quaMo">
        <div style="opacity: 1" class="WhatsappChat__Message-sc-1wqac52-4 kAZgZq">
           
            <div class="WhatsappChat__Text-sc-1wqac52-2 iSpIQi ">
               
                <p v-html="props.msgInfo.message" class="preserve-whitespace text-subtitle-2 " ></p>
            </div>
            
        </div>
    </div>
</template>
<script setup lang="ts">
interface Props {
    msgInfo: {
        message: string,
        header: string,
        footer: string,
        msgType: string
    }
}
const props = defineProps<Props>()
// console.log(props.msgInfo)
</script>

<style scoped>
.preserve-whitespace {
    white-space: pre-wrap;
    word-break:keep-all;
}

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
    width: 350px;
    border-radius: 10px;
    box-shadow: 0 1px 15px rgba(32, 33, 36, 0.28);
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
    box-shadow: 0 10px 10px rgba(32, 33, 36, 0.28);
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
        width: 400px;
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
    background-color: #e6ddd4;
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
    z-index: 0;
    opacity: 0.08;
    background-image: url("https://elfsight.com/assets/chats/patterns/whatsapp.png");
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
    box-shadow: rgba(0, 0, 0, 0.13) 0px 1px 0.5px;
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
    padding: 7px 5px 6px;
    background-color: #fff;
    border-radius: 0px 12px 12px;
    position: relative;
    transition: all 0.3s ease 0s;
    opacity: 0;
    transform-origin: center top 0px;
    /* z-index: 2; */
    box-shadow: #00000021 0px 1px 0.5px;
    margin-top: 20px;
    max-width: 100%;
    display: inline-block;
}

.kAZgZq::before {
    position: absolute;
    background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAmCAMAAADp2asXAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAACQUExURUxpccPDw9ra2m9vbwAAAAAAADExMf///wAAABoaGk9PT7q6uqurqwsLCycnJz4+PtDQ0JycnIyMjPf3915eXvz8/E9PT/39/RMTE4CAgAAAAJqamv////////r6+u/v7yUlJeXl5f///5ycnOXl5XNzc/Hx8f///xUVFf///+zs7P///+bm5gAAAM7Ozv///2fVensAAAAvdFJOUwCow1cBCCnqAhNAnY0WIDW2f2/hSeo99g1lBYT87vDXG8/6d8oL4sgM5szrkgl660OiZwAAAHRJREFUKM/ty7cSggAABNFVUQFzwizmjPz/39k4YuFWtm55bw7eHR6ny63+alnswT3/rIDzUSC7CrAziPYCJCsB+gbVkgDtVIDh+DsE9OTBpCtAbSBAZSEQNgWIygJ0RgJMDWYNAdYbAeKtAHODlkHIv997AkLqIVOXVU84AAAAAElFTkSuQmCC");
    background-position: 50% 50%;
    background-repeat: no-repeat;
    background-size: contain;
    content: "";
    top: 0px;
    left: 12px;
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
    border-radius: 10px;
}

.cqCDVm {
    text-align: right;
    margin-top: 4px;
    font-size: 12px;
    line-height: 16px;
    color: rgba(17, 17, 17, 0.5);
    margin-right: -8px;
    margin-bottom: -4px;
}
</style>
