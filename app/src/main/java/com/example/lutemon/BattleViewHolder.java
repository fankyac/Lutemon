package com.example.lutemon;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for displaying Lutemons in battle
 */
public class BattleViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView healthText;
    private final TextView attackText;
    private final TextView defenseText;
    private final TextView experienceText;
    private final LutemonShapeView shapeView;

    public BattleViewHolder(View view) {
        super(view);
        nameText = view.findViewById(R.id.lutemon_name);
        healthText = view.findViewById(R.id.lutemon_health);
        attackText = view.findViewById(R.id.lutemon_attack);
        defenseText = view.findViewById(R.id.lutemon_defense);
        experienceText = view.findViewById(R.id.lutemon_experience);
        shapeView = view.findViewById(R.id.lutemon_shape);
    }

    /**
     * Binds Lutemon data to this view holder
     */
    public void bind(final Lutemon lutemon) {
        shapeView.setLutemon(lutemon);
        nameText.setText(itemView.getContext().getString(R.string.lutemon_name_format,
                lutemon.getName(), lutemon.getColor()));
        healthText.setText(itemView.getContext().getString(R.string.health_display,
                lutemon.getHealth(), lutemon.getMaxHealth()));
        attackText.setText(itemView.getContext().getString(R.string.attack_display,
                lutemon.getTotalAttack()));
        defenseText.setText(itemView.getContext().getString(R.string.defense_display,
                lutemon.getDefense()));
        experienceText.setText(itemView.getContext().getString(R.string.experience_display,
                lutemon.getExperience()));
    }
}