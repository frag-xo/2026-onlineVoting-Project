<template>
    <div class="main">
        <div class="leftDiv">
            <area-list></area-list>
        </div>
        <div class="rightDiv">
            <div class="rightDiv1">
                <enterExitReport></enterExitReport>
            </div>
            <div class="rightDiv2">
                <enterExitReport2></enterExitReport2>
            </div>

        </div>
    </div>
</template>
<script lang="ts">

</script>
<style scoped lang="stylus">

    div
        border dotted 1px

    .main
        margin-left auto
        margin-right auto
        width 1000px
        div
            float left
    .leftDiv
        width 700px;
        height 500px
    .rightDiv
        margin-left 18px;
        width 280px;
        height 500px
        .rightDiv1,.rightDiv2
            width 280px;
            height 240px
            margin-top 5px;
</style>