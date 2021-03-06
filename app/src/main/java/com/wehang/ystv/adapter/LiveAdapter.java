package com.wehang.ystv.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.ui.AddLiveActivity;
import com.wehang.ystv.ui.LiveDetails;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.ui.MyVideoActivity;
import com.wehang.ystv.ui.PayActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.ui.VideoDetails;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class LiveAdapter extends BaseAdapter {
    private Activity context;
    private LayoutInflater inflater;
    private List<Lives> nearLives;
    protected long lastClickTime = 0;
    protected final int TIME_INTERVAL = 500;
    int type = 0;

    //1.???????????????2??????????????????3???????????????,4,????????????,5.????????????,6.????????????
    public LiveAdapter(Activity context, List<Lives> nearLives, int type) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.nearLives = nearLives;
        this.type = type;
    }

    @Override
    public int getCount() {
        return nearLives != null ? nearLives.size() : 0;
    }

    @Override
    public Lives getItem(int position) {
        return nearLives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LiveAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new LiveAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_live, null);
           /*
            holder.buyCount= (TextView) convertView.findViewById(R.id.user_gmsl_itemlive);*/
            //holder.zsCount= (TextView) convertView.findViewById(R.id.user_zsc_itemlive);
            holder.title = (TextView) convertView.findViewById(R.id.title_item_live);
            holder.live_what = (TextView) convertView.findViewById(R.id.live_what);
            holder.user_bg = (ImageView) convertView.findViewById(R.id.user_bg);
            holder.user_touxiang = (ImageView) convertView.findViewById(R.id.user_tx_itemlive);
            holder.name = (TextView) convertView.findViewById(R.id.user_name_itemlive);
            holder.zw = convertView.findViewById(R.id.user_zw_itemlive);
            holder.ngk = convertView.findViewById(R.id.ngk);
            holder.openTime = convertView.findViewById(R.id.open_time);
            holder.rqz = convertView.findViewById(R.id.rqz);
            holder.live_todo = convertView.findViewById(R.id.live_todo);
            holder.live_into = convertView.findViewById(R.id.live_into);
            holder.price = convertView.findViewById(R.id.live_price);
            holder.yiyuanname = convertView.findViewById(R.id.user_yiyuanm_itemlive);
            holder.isVipImg = convertView.findViewById(R.id.isVipImg);

            holder.liveItem = convertView.findViewById(R.id.live_item_toDo);

            holder.live_copy = convertView.findViewById(R.id.live_copy);
            convertView.setTag(holder);
        } else {
            holder = (LiveAdapter.ViewHolder) convertView.getTag();
        }
        if (nearLives.size() == 0) {
            return convertView;
        }
        final Lives lives = nearLives.get(position);
        if (TextUtils.isEmpty(lives.userId)) {
            lives.userId = UserLoginInfo.getUserInfo().userId;
        }
        Glide.with(context).load(UrlConstant.IP + lives.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_touxiang);
        Glide.with(context).load(UrlConstant.IP + lives.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(holder.user_bg);
        if (type!=6){
            //???????????????
            holder.user_touxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //??????????????????
                    Intent intent = new Intent(context, UserHomeActivty.class);
                    Bundle bundle = new Bundle();
                    UserInfo userInfo = new UserInfo();
                    userInfo.userId = lives.userId;
                    bundle.putSerializable("data", userInfo);
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                }
            });
        }else {
            holder.user_touxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //??????????????????
                   return;
                }
            });
        }

        if (lives.isVip == 0) {
            holder.isVipImg.setVisibility(View.GONE);
        } else {
            holder.isVipImg.setVisibility(View.VISIBLE);
        }
        holder.name.setText(lives.name);
        holder.yiyuanname.setText(lives.hospital);
        holder.zw.setText("(" + lives.title + ")");
        holder.ngk.setText(lives.classification);
        holder.title.setText(lives.sourceTitle);
        holder.openTime.setText(lives.startTime + "");
        holder.price.setText("???" + (lives.price / 100) + ".00");
        holder.rqz.setText(lives.wacthNum + "");

        if (type == 3) {
            holder.live_todo.setText("????????????");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.red));
            if (lives.sourceType == 1) {
                holder.live_what.setText("??????");
            } else if (lives.sourceType == 2) {
                holder.live_what.setText("??????");
            } else if (lives.sourceType == 3) {
                holder.live_what.setText("??????");
            } else if (lives.sourceType == 4) {
                holder.live_what.setText("??????");
            }
        } else {
            if (lives.sourceType == 1 || lives.sourceType == 2) {

                //???????????????????????????????????????????????????
                if (type == 1) {
                    int sourceStauts = nearLives.get(position).sourceStauts;
                    if (lives.sourceType == 2) {
                        if (sourceStauts == 0) {
                            holder.live_what.setText("?????????");
                            holder.live_todo.setText("??????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        } else if (sourceStauts == -1) {
                            holder.live_what.setText("??????");
                            holder.live_todo.setText("??????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        } else if (sourceStauts == -2) {
                            holder.live_what.setText("??????");
                            holder.live_todo.setText("??????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        } else {
                            //????????????
                            holder.live_what.setText("????????????");
                            holder.live_todo.setText("????????????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        }
                    } else {
                        holder.live_what.setText("?????????");
                        //????????????
                        holder.live_todo.setText("????????????");
                        holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                    }
                } else {
                    if (lives.sourceType == 1) {
                        holder.live_what.setText("??????");
                    } else {
                        holder.live_what.setText("??????");
                    }

                    if (lives.userId.equals(UserLoginInfo.getUserInfo().userId)) {

                        int sourceType = nearLives.get(position).sourceType;
                        int sourceStauts = nearLives.get(position).sourceStauts;
                        if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                            //????????????
                            holder.live_todo.setText("??????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        } else {
                            holder.live_todo.setText("????????????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        }

                    } else {
                        if (lives.isBuy == 0 && lives.price > 0) {
                            if (lives.sourceType == 1) {
                                holder.live_todo.setText("????????????");
                            } else {
                                holder.live_todo.setText("????????????");
                            }
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.red));
                        } else {
                            holder.live_todo.setText("????????????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        }
                    }
                }



                /*holder.live_todo.setText("??????");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*/
            }
       /*     if (lives.sourceType==3){
                //????????????,????????????????????????
                holder.live_what.setVisibility(View.VISIBLE);
                holder.live_what.setText("??????");
                //????????????
                holder.live_todo.setText("????????????");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));
                //????????????
                holder.live_todo.setText("????????????");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
                //????????????
                holder.live_todo.setText("????????????");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.green));

            }*/
            if (lives.sourceType == 4 || lives.sourceType == 3) {
                holder.live_what.setVisibility(View.VISIBLE);
                if (lives.sourceType == 3) {
                    holder.live_what.setText("??????");
                } else {
                    holder.live_what.setText("??????");
                }
                //????????????????????????????????????????????????

                //????????????,????????????????????????
                //holder.live_todo.setText("????????????");

                /*holder.live_todo.setText("??????");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*/
                if (lives.userId.equals(UserLoginInfo.getUserInfo().userId)) {
                    if (lives.sourceType == 3) {
                        int sourceStauts = nearLives.get(position).sourceStauts;
                        if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                            //????????????
                            holder.live_todo.setText("??????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        } else {
                            holder.live_todo.setText("????????????");
                            holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                        }
                    } else {
                        holder.live_todo.setText("????????????");
                        holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                    }
                } else {
                    if (lives.isBuy == 0 && lives.price > 0) {
                        holder.live_todo.setText("????????????");
                        holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.red));
                    } else {
                        holder.live_todo.setText("????????????");
                        holder.live_todo.setBackground(ContextCompat.getDrawable(context, R.drawable.blue));
                    }

                }

            }
        }


        holder.live_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 3) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserLoginInfo.getUserToken());
                    params.put("sourceId", lives.sourceId);
                    params.put("type", 0 + "");
                    final CustomProgressDialog dialog = CustomProgressDialog.show(context, "?????????", true, null);
                    HttpTool.doPost1(context, UrlConstant.COLLECT, params, true, new TypeToken<BaseResult<BaseData>>() {
                    }.getType(), new HttpTool.OnResponseListener1() {


                        @Override
                        public void onSuccess(String string) {
                            dialog.dismiss();
                            nearLives.remove(position);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int errorCode) {
                            ToastUtil.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    int sourceType = nearLives.get(position).sourceType;
                    int sourceStauts = nearLives.get(position).sourceStauts;
                    if (nearLives.get(position).userId.equals(UserLoginInfo.getUserInfo().userId)) {
                        //?????????
                        //??????????????????
                        Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                        if (sourceType == 1 || sourceType == 2) {
                            if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                                //????????????
                                Intent intent = new Intent(context, AddLiveActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                 /* if (nearLives.get(i-1).sourceType==1){

                  }else if (nearLives.get(i-1).sourceType==2){

                  }*/
                            } else {
                                getLiveNeed(nearLives.get(position).sourceId);
                            }
                        } else if (sourceType == 3 || sourceType == 4) {
                            //??????????????????????????????
                            // ???????????????i???1???????????????0???????????????i-1
                            Intent intent;
                            if (sourceType == 4) {
                                intent = new Intent(context, VideoDetails.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            } else {
                                //????????????
                                if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                                    intent = new Intent(context, AddLiveActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("data", nearLives.get(position));
                                    intent.putExtra("bundle", bundle);
                                    context.startActivity(intent);
                                } else {
                                    intent = new Intent(context, VideoDetails.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("data", nearLives.get(position));
                                    intent.putExtra("bundle", bundle);
                                    context.startActivity(intent);
                                }

                            }
                            return;
                        }
                    } else {

                        //?????????

                        //??????????????????
                        if (sourceType == 1 || sourceType == 2) {
                            if (lives.isBuy == 0 && lives.price > 0 && UserLoginInfo.getUserInfo().isVip == 0) {
                                Intent intent = new Intent(context, PayActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", lives);
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            } else if (lives.isBuy == 1) {
                                //??????????????????
                                Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                                enterRoom(lives);
                            } else if (lives.isBuy == 0 && lives.price == 0 || UserLoginInfo.getUserInfo().isVip == 1) {
                                creatOrder(lives);
                                //??????????????????
                                Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                                enterRoom(lives);
                            }

                        } else if (sourceType == 3 || sourceType == 4) {
                            // ???????????????i???1???????????????0???????????????i-1
                            //??????????????????
                            //Utils.addWatchHistory(context,UserLoginInfo.getUserToken(),nearLives.get(position).sourceId);
                            // ???????????????i???1???????????????0???????????????i-1
                            if (lives.isBuy == 0 && lives.price > 0) {
                                Intent intent = new Intent(context, PayActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", lives);
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            } else if (lives.isBuy == 1) {
                                //??????????????????
                                Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                                Intent intent = new Intent(context, VideoDetails.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            } else if (lives.isBuy == 0 && lives.price == 0) {
                                creatOrder(lives);
                                Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                                Intent intent = new Intent(context, VideoDetails.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            }

                        }
                    }
                }


            }
        });


        holder.liveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sourceType = nearLives.get(position).sourceType;
                int sourceStauts = nearLives.get(position).sourceStauts;
                if (nearLives.get(position).userId.equals(UserLoginInfo.getUserInfo().userId)) {
                    //?????????
                    //??????????????????
                    Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                    if (sourceType == 1 || sourceType == 2) {
                        if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                            //????????????
                            Intent intent = new Intent(context, AddLiveActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", nearLives.get(position));
                            intent.putExtra("bundle", bundle);
                            context.startActivity(intent);
                 /* if (nearLives.get(i-1).sourceType==1){

                  }else if (nearLives.get(i-1).sourceType==2){

                  }*/
                        } else {
                            getLiveNeed(nearLives.get(position).sourceId);
                        }
                    } else if (sourceType == 3 || sourceType == 4) {
                        //??????????????????????????????
                        // ???????????????i???1???????????????0???????????????i-1
                        Intent intent;
                        if (sourceType == 4) {
                            intent = new Intent(context, VideoDetails.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", nearLives.get(position));
                            intent.putExtra("bundle", bundle);
                            context.startActivity(intent);
                        } else {
                            //????????????
                            if (sourceStauts == 0 || sourceStauts == -1 || sourceStauts == -2) {
                                intent = new Intent(context, AddLiveActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            } else {
                                intent = new Intent(context, VideoDetails.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", nearLives.get(position));
                                intent.putExtra("bundle", bundle);
                                context.startActivity(intent);
                            }
                        }
                        return;
                    }
                } else {
                    //?????????
                    //??????????????????
                    Utils.addWatchHistory(context, UserLoginInfo.getUserToken(), nearLives.get(position).sourceId);
                    //??????????????????
                    if (sourceType == 1 || sourceType == 2) {
                        Intent intent = new Intent(context, LiveDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", nearLives.get(position));
                        bundle.putSerializable("live", nearLives.get(position));
                        intent.putExtra("bundle", bundle);

                        context.startActivity(intent);
             /* enterRoom(nearLives.get(i-1).sourceId);
                LogUtils.i("OnItemClickListener",nearLives.get(i-1).sourceId);*/
                        // ???????????????i???1???????????????0???????????????i-1
                        //??????????????????
                    } else if (sourceType == 3 || sourceType == 4) {
                        // ???????????????i???1???????????????0???????????????i-1
                        //??????????????????
                        //Utils.addWatchHistory(context,UserLoginInfo.getUserToken(),nearLives.get(position).sourceId);
                        // ???????????????i???1???????????????0???????????????i-1
                        Intent intent = new Intent(context, VideoDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", nearLives.get(position));
                        intent.putExtra("bundle", bundle);
                        context.startActivity(intent);
                    }
                }
            }
        });

        if (type==1){
          if (lives.sourceStauts == 0 || lives.sourceStauts == -1 || lives.sourceStauts == -2){

          } else {
              if(lives.sourceType==2){
                  holder.live_copy.setVisibility(View.VISIBLE);
                  holder.live_copy.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          if (lives.pushUrl !=null){
                              ClipboardUtils.copyText(lives.pushUrl);
                              ToastUtil.makeText(context,"?????????????????????????????????",Toast.LENGTH_LONG).show();
                          }else {
                              ToastUtil.makeText(context,"??????????????????",Toast.LENGTH_LONG).show();
                          }
                      }
                  });
              }
          }

        }
        return convertView;
    }

    private class ViewHolder {
        ImageView user_touxiang, user_bg, isVipImg;

        TextView name, zw, yiyuanname, ngk, buyCount, title, live_what, openTime, rqz, price, live_todo, live_into;
        LinearLayout liveItem;
        //??????????????????
        TextView live_copy;
    }

    public void setData(List<Lives> nearLives) {
        this.nearLives.clear();
        this.nearLives.addAll(nearLives);
        notifyDataSetChanged();
    }

    private void enterRoom(final Lives lives) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "?????????...", true, null);
        HttpTool.doPost(context, UrlConstant.ENTERROOM, params, true, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                UserInfo userInfo = (UserInfo) data;
                if (userInfo != null) {
                    Intent intent = new Intent(context, LiveWatchNewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", userInfo);
                    bundle.putSerializable("live", lives);
                    intent.putExtra("bundle", bundle);
                    intent.putExtra("sourceId", lives.sourceId);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    //????????????????????????
    private CustomProgressDialog dialog;
    public LiveNeed liveNeed;

    private void getLiveNeed(String sourceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserInfo().token);
        params.put("sourceId", sourceId);
        dialog = CustomProgressDialog.show(context, "?????????...", true, null);
        HttpTool.doPost(context, UrlConstant.FORESHOWBEGINLIVE, params, true, new TypeToken<BaseResult<LiveNeed>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                liveNeed = (LiveNeed) data;
                LogUtils.i("LiveNeed", liveNeed.toString());
                if (liveNeed != null) {
                    liveNeed.isPhonePush=1;
                    checkPermissions();
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    public void checkPermissions() {
        /*if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(context, mPermissionList, 0);
        } else {
            // ???????????????i???1???????????????0???????????????i-1

        }*/
        startLive();
    }

    public void startLive() {
        Intent intent = new Intent(context, LivePushActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", liveNeed);
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }

    //????????????end
    private void creatOrder(Lives lives) {
        setPushTag(lives);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        HttpTool.doPost(context, UrlConstant.BUTLIVE, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    private void setPushTag(Lives lives){
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(lives.sourceId);
        JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }
}