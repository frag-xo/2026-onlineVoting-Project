/**
 * Independent time operation tool to facilitate subsequent switch to dayjs
 */
import dayjs from 'dayjs'
import  moment from 'moment';



const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss'
const DATE_FORMAT = 'YYYY-MM-DD'

export function formatToDateTime(dateTimeStr): string {
  const formattedDateTime = moment(dateTimeStr).format(DATE_TIME_FORMAT);
  return formattedDateTime
}

export function formatToDate(dateStr): string {
  const formattedDate = moment(dateStr).format(DATE_FORMAT);
  return formattedDate
}

export const dateUtil = dayjs
