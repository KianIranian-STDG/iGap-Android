package com.iGap.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityMain;
import com.iGap.interfaces.OnChannelCheckUsername;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.proto.ProtoChannelCheckUsername;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChannelAvatarAdd;
import com.iGap.request.RequestChannelCheckUsername;
import com.iGap.request.RequestClientGetRoom;

import static com.iGap.R.id.fragmentContainer;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateChannel extends Fragment implements OnChannelCheckUsername {

    private Long roomId;
    private String inviteLink;
    public static final int PRIVATE = 0;
    public static final int PUBLIC = 1;
    private EditText edtLink;
    private RadioButton raPublic;
    private RadioButton raPrivate;
    private TextInputLayout txtInputLayout;
    private TextView txtFinish;
    private String token;
    private boolean existAvatar;
    private ProgressBar prgWaiting;


    public FragmentCreateChannel(
    ) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_channel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onChannelCheckUsername = this;

        if (getArguments() != null) {
            roomId = getArguments().getLong("ROOMID");
            inviteLink = getArguments().getString("INVITE_LINK");
            token = getArguments().getString("TOKEN");
            existAvatar = getArguments().getBoolean("AVATAR");

        }

        prgWaiting = (ProgressBar) view.findViewById(R.id.fch_prgWaiting_addContact);
        prgWaiting.setVisibility(View.GONE);

        ViewGroup vgRoot = (ViewGroup) view.findViewById(R.id.fch_root);
        vgRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView txtBack = (TextView) view.findViewById(R.id.fch_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentCreateChannel.this)
                        .commit();
            }
        });

        TextView txtCancel = (TextView) view.findViewById(R.id.fch_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentCreateChannel.this)
                        .commit();
            }
        });

        txtFinish = (TextView) view.findViewById(R.id.fch_txt_finish);
        txtFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((raPrivate.isChecked() || edtLink.getText().toString().length() > 0) && roomId > 0) {

                    showProgressBar();
                    getRoom(roomId, ProtoGlobal.Room.Type.CHANNEL);

                }
            }
        });

        txtInputLayout = (TextInputLayout) view.findViewById(R.id.fch_txtInput_nikeName);
        edtLink = (EditText) view.findViewById(R.id.fch_edt_link);

        edtLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!raPrivate.isChecked())
                    new RequestChannelCheckUsername().channelCheckUsername(roomId, charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.fch_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setInviteLink();
            }
        });


        raPublic = (RadioButton) view.findViewById(R.id.fch_radioButton_Public);
        raPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        raPrivate = (RadioButton) view.findViewById(R.id.fch_radioButton_private);
        raPrivate.setChecked(true);
        raPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setInviteLink();
    }

    private void getRoom(final Long roomId, final ProtoGlobal.Room.Type type) {
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                hideProgressBar();
                                if (existAvatar) {
                                    showProgressBar();
                                    new RequestChannelAvatarAdd().channelAvatarAdd(roomId, token);

                                } else {
                                    Fragment fragment = ContactGroupFragment.newInstance();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("RoomId", roomId);

                                    if (room.getType() == ProtoGlobal.Room.Type.GROUP) {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    } else {
                                        bundle.putString("LIMIT", room.getGroupRoomExtra().getParticipantsCountLimitLabel());
                                    }
                                    bundle.putString("TYPE", type.toString());
                                    bundle.putBoolean("NewRoom", true);
                                    fragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                            .addToBackStack(null)
                                            .replace(fragmentContainer, fragment)
                                            .commitAllowingStateLoss();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentCreateChannel.this).commit();
                                    ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }

                            }
                        });
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                hideProgressBar();

                if (majorCode == 610) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_610), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 611) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_611), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 612) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_612), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 613) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_613), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 614) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_614), Snackbar.LENGTH_LONG);

                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }

            @Override
            public void onTimeOut() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });

            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId);
    }

    private void setInviteLink() {

        if (raPrivate.isChecked()) {
            edtLink.setText(inviteLink);
            edtLink.setEnabled(false);
            txtFinish.setEnabled(true);
            txtFinish.setTextColor(getResources().getColor(R.color.toolbar_background));
            txtInputLayout.setErrorEnabled(true);
            txtInputLayout.setError("");

        } else if (raPublic.isChecked()) {
            edtLink.setEnabled(true);
            edtLink.setText("");
        }
    }

    @Override
    public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                    txtFinish.setEnabled(true);
                    txtInputLayout.setErrorEnabled(true);
                    txtInputLayout.setError("");
                } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                    txtFinish.setEnabled(false);
                    txtFinish.setTextColor(getResources().getColor(R.color.gray_6c));
                    txtInputLayout.setErrorEnabled(true);
                    txtInputLayout.setError("INVALID");

                } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                    txtFinish.setEnabled(false);
                    txtFinish.setTextColor(getResources().getColor(R.color.gray_6c));
                    txtInputLayout.setErrorEnabled(true);
                    txtInputLayout.setError("TAKEN");
                }
            }
        });

    }

    @Override
    public void onError(int majorCode, int minorCode) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);

                snack.setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    @Override
    public void onTimeOut() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                snack.setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaiting.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaiting.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

}
