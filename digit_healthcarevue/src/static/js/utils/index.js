let FD = (function () {
  function FD () {}
  // From表单验证规则  可用于公用的校验部分
  FD.Fd = function () {
    return {
      dateFormatCN(cellValue,format,fecha) {
        return cellValue ? fecha.format(new Date(cellValue), format) : '';
      },
      initCNDate(date){
        let y = date.getFullYear()
        let m = date.getMonth() + 1
        m = m < 10 ? ('0' + m) : m
        let d = date.getDate()
        d = d < 10 ? ('0' + d) : d
        let h =date.getHours()
        h = h < 10 ? ('0' + h) : h
        let M =date.getMinutes()
        M = M < 10 ? ('0' + M) : M
        let s =date.getSeconds()
        s = s < 10 ? ('0' + s) : s
        let initdate= y + '-' + m + '-' + d;
        return initdate;
      },
      initCNTime(date){
        let y = date.getFullYear()
        let m = date.getMonth() + 1
        m = m < 10 ? ('0' + m) : m
        let d = date.getDate()
        d = d < 10 ? ('0' + d) : d
        let h =date.getHours()
        h = h < 10 ? ('0' + h) : h
        let M =date.getMinutes()
        M = M < 10 ? ('0' + M) : M
        let s =date.getSeconds()
        s = s < 10 ? ('0' + s) : s
        let dateTime= y + '-' + m + '-' + d + ' ' + h + ':' + M + ':' + s;
        return dateTime;
      },
      dateFormat(row) {
        var t = new Date(row.createTime)// row 表示一行数据, createTime 表示要格式化的字段名称
        if (!t) {
          return ''
        }
        let year = t.getFullYear()
        let month = this.dateIfAddZero(t.getMonth() + 1)
        let day = this.dateIfAddZero(t.getDate())
        let hours = this.dateIfAddZero(t.getHours())
        let minutes = this.dateIfAddZero(t.getMinutes())
        let seconds = this.dateIfAddZero(t.getSeconds())
        return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds
      },

      dateIfAddZero(time) {
        return time < 10 ? '0' + time : time
      }
    }
  }

  return FD
}())

exports.FD = FD

