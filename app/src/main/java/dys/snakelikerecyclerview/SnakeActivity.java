package dys.snakelikerecyclerview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dys on 2016/12/12 0012.
 * 蛇形recyclerView
 */
public class SnakeActivity extends Activity {
    private static final int COLUMN_CONT = 3;
    //    private static final String[] ITEMS = new String[16];
    private List<String> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 17; i++) {
            mItems.add(String.valueOf(i));
        }
        mItems = sortData(mItems, COLUMN_CONT);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMN_CONT, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ConnectorDecoration(this));
        recyclerView.setAdapter(new MyAdapter(this));
    }

    /**
     * 按蛇形布局排序数据
     *
     * @param list
     * @param columnCount
     * @return
     */
    private List<String> sortData(List<String> list, int columnCount) {
        //总行数(奇数个元素时，值为为总行数-1)
        int sumLine = list.size() / columnCount;
        if (list.size() % columnCount != 0) {
            sumLine++;
        }
        //当size不能整除column时，最后一行的剩余元素个数；如果能整除，则不走
        int lastLineNodeCount = list.size() % columnCount;
        //当前行号
        int currentLineNumber;
        for (int i = 0; i < list.size(); i++) {
            currentLineNumber = i / columnCount;
            String temp = list.get(i);

            if (lastLineNodeCount == 0) {
                if (isOddNumber(currentLineNumber) && i % columnCount < columnCount / 2 && currentLineNumber <= sumLine - 1) {
                    list.set(i, list.get(columnCount - 1 - (i % columnCount) + columnCount * currentLineNumber));
                    list.set(columnCount - 1 - i % columnCount + columnCount * currentLineNumber, temp);
                }
            } else {
                /**
                 * 判断条件：1.奇数行
                 *          2.小于columnCount / 2 前后翻转
                 *          3.当前行不是最后一行
                 * */
                if (isOddNumber(currentLineNumber) && i % columnCount < columnCount / 2 && currentLineNumber < sumLine - 1) {
                    list.set(i, list.get(columnCount - 1 - (i % columnCount) + columnCount * currentLineNumber));
                    list.set(columnCount - 1 - i % columnCount + columnCount * currentLineNumber, temp);
                }
                /**
                 * 判断条件：1.奇数行
                 *          2.小于lastLineNodeCount / 2 前后翻转
                 *          3.当前行是最后一行。
                 * */
                else if (isOddNumber(currentLineNumber) && ((i % columnCount) % lastLineNodeCount) < (lastLineNodeCount / 2) && currentLineNumber == sumLine - 1) {
                    list.set(i, list.get(lastLineNodeCount - 1 - ((i % columnCount) % lastLineNodeCount) + columnCount * currentLineNumber));
                    list.set(lastLineNodeCount - 1 - ((i % columnCount) % lastLineNodeCount) + columnCount * currentLineNumber, temp);
                }
            }
        }
        return list;
    }

    /**
     * 判断奇偶
     */
    public boolean isOddNumber(int number) {
        return number % 2 != 0; //奇数返回true
    }

    /**
     * 适配器
     */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemHolder> {
        private LayoutInflater mLayoutInflater;
        private Context mContext;

        public MyAdapter(Context context) {
            mContext = context;
            List<String> temp = new ArrayList<>();
            if (mItems.size() % COLUMN_CONT != 0 && (mItems.size() / COLUMN_CONT) % 2 != 0) {
                temp.addAll(mItems.subList(0, mItems.size() - (mItems.size()) % COLUMN_CONT)); //16-16%3=15 add前15个的数据
                for (int i = 0; i < COLUMN_CONT - (mItems.size()) % COLUMN_CONT; i++) { //2 add2个填充物
                    temp.add("placeHolder");
                }
                temp.addAll(mItems.subList(mItems.size() - (mItems.size()) % COLUMN_CONT, mItems.size()));//16-16%3=15 add数组中剩余的数据。即第16个
                mItems = temp;
            }
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            if (mItems.get(position).equals("placeHolder")) {
                holder.setText("");
                holder.tv.setBackgroundColor(Color.TRANSPARENT);  //TRANSPARENT
            } else {
                holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                holder.setText(mItems.get(position));
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "position=" + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            private TextView tv;

            public ItemHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.item_tv);
            }

            public void setText(CharSequence text) {
                tv.setText(text);
            }
        }

    }

    /**
     * 连接线
     */
    public class ConnectorDecoration extends RecyclerView.ItemDecoration {
        private Paint mLinePaint;
        private int mSpace;

        public ConnectorDecoration(Context context) {
            super();
            mSpace = context.getResources().getDimensionPixelOffset(R.dimen.space_margin);
            int connectorWidth = context.getResources().getDimensionPixelOffset(R.dimen.connector_width);
            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
            //设置连接线宽度
            mLinePaint.setStrokeWidth(connectorWidth);
        }

        /*
        设置每个item之间的边距
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(mSpace, mSpace, mSpace, mSpace);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                int childLeft = manager.getDecoratedLeft(child);
                int childRight = manager.getDecoratedRight(child);
                int childTop = manager.getDecoratedTop(child);
                int childBottom = manager.getDecoratedBottom(child);
                //当前childView x轴方向的中间位置。
                int x = childLeft + ((childRight - childLeft) / 2);
                //当前childView y轴方向的中间位置。
                int y = childBottom + ((childTop - childBottom) / 2);
                int position = parent.getChildViewHolder(child).getLayoutPosition();
                if (!mItems.get(position).equals("placeHolder")) {
                    //过滤出第一列上所有item的position
                    if (position % COLUMN_CONT == 0) {
                        //第一列中奇数行item与下一行第一个item连接,并且当前不是最后一个childView,并且如果当前是最后一行也不画纵向连接线
                        if ((position / COLUMN_CONT) % 2 == 1 && position < mItems.size() - COLUMN_CONT) {
                            c.drawLine(x, childBottom - mSpace, x, childBottom + mSpace, mLinePaint);
                        }
                        //过滤出最后一列上所有item的position
                    } else if (position % COLUMN_CONT == (COLUMN_CONT - 1)) {
                        //偶数行，最后一列向下画连接线，并且当前不是最后一个childView
                        if ((position / COLUMN_CONT) % 2 == 0 && (position != mItems.size() - 1)) {
                            c.drawLine(x, childBottom - mSpace, x, childBottom + mSpace, mLinePaint);
                        }
                    }
                    //当前childView不是每一行的最后一列，并且不是最后一个，画横向的连接线。
                    if ((position % COLUMN_CONT != (COLUMN_CONT - 1)) && (position != mItems.size() - 1)) {
                        c.drawLine(childRight - mSpace, y, childRight + mSpace, y, mLinePaint);
                    }
                }
            }
        }
    }
}
