<template>

    <b-container fluid class="p-4" id="reputation-badge-container">


         <b-row>
               <b-col md="4" class="text-center no-padding"
                         v-for="badge in badges" :key="badge">
                         <b-img thumbnail fluid :id="'reputation'+badge.id" :src="badge.url" alt="Thumbnail" class="m-1"  width="50" height="50" />
                     <!-- <b-btn :id="'reputation'+badge.id" variant="primary">
                        {{ badge.id }}
                      </b-btn> -->
                      <b-popover :target="'reputation'+badge.id"
                                 :placement="'left'"
                                 triggers="hover focus"
                                 :content="`${badge.description}`">
                                 <div class="level-badges">
                                        1<br><span>Level</span>
                                    </div>
                                    <div class="title-badges">{{badge.title}}</div>
                                    <div class="cat-badges">{{badge.zone}} </div>                          
                                    <div class="date-badges">{{badge.createdDate}}</div>
                                    <div class="desc-badges">{{badge.description}} </div>
                                    <div class="prog-point">
                                        <div class="first-number">200</div><hr class="interval"><div class="last-number">500</div>
                                    </div>                                     
                      </b-popover>
                      </b-col>
                      
                      
            </b-row>

        <!--       
        <h5 class="my-3">Placement</h5>
        <b-row>
            <b-col md="4" class="py-4 text-center" v-for="placement in placements" :key="placement">
                <b-btn :id="'exPopover1-'+placement" variant="primary">
                    {{ placement }}
                </b-btn>
                <b-popover :target="'exPopover1-'+placement" :placement="placement" title="Popover!" triggers="hover focus" :content="`Placement ${placement}`">
                </b-popover>
            </b-col>
        </b-row>
    -->


    </b-container>
</template>
<script>

    import Vue from 'vue'
    import BootstrapVue from 'bootstrap-vue'
    import { Popover } from 'bootstrap-vue/es/components';
    import { Image } from 'bootstrap-vue/es/components';
    import axios from 'axios';
    import 'bootstrap/dist/css/bootstrap.css'
    import 'bootstrap-vue/dist/bootstrap-vue.css'
    Vue.use(BootstrapVue);
    Vue.use(Popover);
    Vue.use(Image);

    const initialData = () => {
        return {
            badges: [],
           

        }
    }

    export default {
        data: initialData,
        methods: {
            showBadgeDetail(badgeDTO) {
                axios.get(`/rest/gamification/reputation/update`, badgeDTO)
                    .then(response => {

                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            }


        },

        created() {
            axios.get(`/rest/gamification/reputation/badge/all`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    //this.posts = response.data
                    console.log(JSON.stringify(response.data))
                    this.badges = response.data;
                })
                .catch(e => {
                    this.errors.push(e)
                })
        }
    }
</script>

<style scoped>

@font-face {
    font-family: 'open_sanssemibold';
    src: url('/gamification-portlets/skin/fonts/opensans-semibold-webfont.woff2') format('woff2'),
         url('/gamification-portlets/skin/fonts/opensans-semibold-webfont.woff') format('woff');
    font-weight: normal;
    font-style: normal;

}
@font-face {
    font-family: 'open_sansbold';
    src: url('/gamification-portlets/skin/fonts/opensans-bold-webfont.woff2') format('woff2'),
         url('/gamification-portlets/skin/fonts/opensans-bold-webfont.woff') format('woff');
    font-weight: normal;
    font-style: normal;

}
@font-face {
    font-family: 'open_sansregular';
    src: url('/gamification-portlets/skin/fonts/opensans-regular-webfont.woff2') format('woff2'),
         url('/gamification-portlets/skin/fonts/opensans-regular-webfont.woff') format('woff');
    font-weight: normal;
    font-style: normal;

}

#reputation-badge-container{
    padding-right: 10px !important;
    padding-left: 10px !important;
} 
.title-badges{
    color:#333333;
    font-family: 'open_sanssemibold';
    font-size: 14px;
    font-weight: bold;
    padding: 5px 0px;
}
.cat-badges{
    font-family: 'open_sansregular';
    color:#333333;
    font-size:11px;
}
.date-badges{
    font-family: 'open_sansregular';
    color:#333333;
    font-size: 11px;  
    padding-bottom: 5px;
}
.desc-badges{
    padding:0px 15px;
    font-family: 'open_sansregular';
    color:#333333;
    padding-bottom: 12px;
}
.level-badges{
    background: #4f6998;
    padding: 14px 14px 6px;
    font-size: 34px;
    color: #fff;
    font-weight: 700;
    display: inline-block;
    line-height: 10px;
    position: relative;
    border-radius: 50%;
    font-family: 'open_sansbold';
}
.level-badges span{
    font-size: 9px;
    font-weight: normal;
    font-family: 'open_sansregular';
}
.prog-point{
    background: #f0f8fe;
    min-height: 46px;
    padding: 7px 0px;
}
.first-number{
    padding: 4px 3px;
    border-radius: 50%;
    background:#60a352;
    color:#fff;
    font-family: 'open_sansregular';
    font-size:12px;
    float:left;
    position: relative;
    top: 3px;
    left: 5px;
    z-index: 10;
}
.last-number{
    font-size: 14px;
    float: left;
    padding: 6px 4px;
    background:#60a352;
    color:#fff;
    font-family: 'open_sansregular';
    border-radius: 50%;
    position: relative;
    z-index: 10;
    border: 3px solid #c7e5c8;
}
.interval{
    height: 2px;
    background: #60a352;
    width: 124px;
    float: left;
    position: relative;
    top: 1px;
}
</style>