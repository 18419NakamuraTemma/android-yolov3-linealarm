from linebot import LineBotApi
from linebot.models import TextSendMessage





def send_message(message):
    CHANNEL_ACCESS_TOKEN = 'Line Access Token'
    line_bot_api = LineBotApi(CHANNEL_ACCESS_TOKEN)
    messages = TextSendMessage(text=message)
    line_bot_api.broadcast(messages=messages)
    return message


