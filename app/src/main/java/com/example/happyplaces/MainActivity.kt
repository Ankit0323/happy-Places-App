package com.example.happyplaces


import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.room.happyPlacesData
import com.example.happyplaces.room.happyPlacesViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MainActivity : AppCompatActivity(), recyclerViewAdapter.click {
    private lateinit var viewModel: happyPlacesViewModel
    lateinit var data:happyPlacesData
    lateinit var item:happyPlacesData
private lateinit var mAdapter:recyclerViewAdapter
    private lateinit var binding :ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.floating.setOnClickListener {
            val intent= Intent(this, happyPlacesActivity::class.java)
            startActivity(intent)
        }
       binding.recycle.layoutManager=LinearLayoutManager(this)
         mAdapter=recyclerViewAdapter(this,this)
        binding.recycle.adapter=mAdapter

        viewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)).get(happyPlacesViewModel::class.java)
        viewModel.getAllData.observe(this, androidx.lifecycle.Observer { list ->
            list?.let {
                mAdapter.updateList(it)

                if (list.size > 0) {
                    binding.recycle.visibility = View.VISIBLE
                    binding.noText.visibility = View.GONE

                } else {
                    binding.noText.visibility = View.VISIBLE
                    binding.recycle.visibility = View.GONE

                }
            }

        }

        )

        var itemTouch= object: ItemTouchHelper.SimpleCallback(0,   ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
          return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {

                    ItemTouchHelper.LEFT -> {

                        item = mAdapter.list.get(position)
                        var id = item.id
                        mAdapter.list.removeAt(position)

                        mAdapter.notifyItemRemoved(position)
                        viewModel.deleteData(item)

                        Snackbar.make(binding.recycle, item.title.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Undo", View.OnClickListener {
                                    mAdapter.list.add(position, item)
                                    item.id = id
                                    viewModel.insertData(item)
                                    //mAdapter.updateList(mAdapter.list)
                                    mAdapter.notifyItemRemoved(position)
                                }).show()

                    }

                }
            }


            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate()


            }

        }
        // for edit
        val itemTouch1= object: ItemTouchHelper.SimpleCallback(0,   ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //here the when block is not necessary
                when (direction) {

                    ItemTouchHelper.RIGHT -> {

                      val adapter=binding.recycle.adapter as recyclerViewAdapter
                        adapter.editItems(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_CODE)






                      /*  Snackbar.make(binding.recycle, item.title.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Undo", View.OnClickListener {
                                    mAdapter.list.add(position, item)
                                   // item.id = id
                                    viewModel.insertData(item)

                                    mAdapter.notifyItemRemoved(position)
                                }).show()*/




                    }

                }
            }


            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.green))
                        .addSwipeRightActionIcon(R.drawable.archive)

                        .create()
                        .decorate()


            }

        }



        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.recycle)
        ItemTouchHelper(itemTouch1).attachToRecyclerView(binding.recycle)





    }


    override fun itemClick(happyPlacesData: happyPlacesData) {
        // data=happyPlacesData
        var intent=Intent(this@MainActivity, happyPlacesDetails::class.java)
        intent.putExtra("FROM_MAIN_TO_DETAILS", happyPlacesData)
        startActivity(intent)
    }
    companion object{
        val ADD_PLACE_CODE=1
       // val EXTRA_PLACE_DETAILS=2
    }
}