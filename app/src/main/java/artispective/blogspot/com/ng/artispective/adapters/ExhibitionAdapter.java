package artispective.blogspot.com.ng.artispective.adapters;

/**
 * Created by Nobest on 13/05/2016.
 */
public class ExhibitionAdapter /*extends RecyclerView.Adapter<ExhibitionAdapter.ViewHolder> */{
  /*  ArrayList<Exhibition> exhibitions;
    private Context context;
    private static final String IMAGE = "https://develop.backendless.com/console/A9055167-61DE-81A4-FF76-A84111205300/appversion/5321A127-7220-D203-FF33-0B546371BB00/rqilcmquprslnyqidkgevpahktoyxcfxizjy/files/view/exhibitionimages/Nigeria.png";

    public ExhibitionAdapter(Context context, ArrayList<Exhibition> exhibitions) {
        this.context = context;
        this.exhibitions = exhibitions;
    }

    @Override
    public ExhibitionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exhibition_layout, parent, false);

        return new ViewHolder(convertView);
    }

    public void addExhibition(Exhibition exhibition) {
        exhibitions.add(exhibition);
        notifyDataSetChanged();
    }

    public void addExhibitions(List<Exhibition> collection) {
        exhibitions.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ExhibitionAdapter.ViewHolder holder, int position) {
        Exhibition exhibition = exhibitions.get(position);
        TextView exhibition_title, exhibition_content, exhibition_date, exhibition_location;
        ImageView exhibition_image;

        exhibition_title = holder.exhibition_title;
        exhibition_content = holder.exhibition_content;
        exhibition_date = holder.exhibition_date;
        exhibition_location = holder.exhibition_location;
        exhibition_image = holder.exhibition_image;

        exhibition_title.setText(exhibition.getaName());
        exhibition_content.setText(exhibition.getDetail());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(exhibition.getExhibition_date());
        exhibition_date.setText(String.format("%s/%s/%s", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)));
        exhibition_location.setText(exhibition.getLocation());

        Picasso.with(context).load(IMAGE)
                .resize(100, 100)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .centerCrop()
                .into(exhibition_image);

    }

    @Override
    public int getItemCount() {
        return exhibitions.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView exhibition_title, exhibition_content, exhibition_date, exhibition_location;
        public ImageView exhibition_image;

        public ViewHolder(View itemView) {
            super(itemView);

            exhibition_title = (TextView) itemView.findViewById(R.id.exhibition_title);
            exhibition_content = (TextView) itemView.findViewById(R.id.exhibition_content);
            exhibition_date = (TextView) itemView.findViewById(R.id.exhibition_date);
            exhibition_location = (TextView) itemView.findViewById(R.id.exhibition_location);
            exhibition_image = (ImageView) itemView.findViewById(R.id.exhibition_image);

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("exhibitions", exhibitions);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            if (!Helper.getUserAdminStatus()) {
                return false;
            }
//            deleteClicked();
            return false;
        }

        private void deleteClicked(LongClickListener longClickListener){
            longClickListener.onDeleteClicked();
        }
    }*/
}
